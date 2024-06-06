package com.unseewr1.leafyreader.fileuri

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import androidx.core.net.toUri
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import com.unseewr1.leafyreader.PdfViewerActivity
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter

class PdfUri(
    context: Context,
    file: File,
) : TypedUri(context, file.toUri()) {

    private val meta: PdfDocument.Meta by lazy {
        getPdfMetadata(context, uri)
    }

    override fun title(): String = meta.title

    //TODO: check if some tricky ways are available
    override fun author(): String = ""
    override fun cover(): Bitmap =
        BitmapFactory.decodeFile(extractFirstPageAsJpg(context, uri).path)

    override fun getOnClickListener() = View.OnClickListener {
        val intent = Intent(context, PdfViewerActivity::class.java)
        intent.putExtra("pdfUri", asUri())
        intent.putExtra("lastPage", getLastPage(context, asUri().path.toString()))
        context.startActivity(intent)
    }

    private fun getLastPage(context: Context, pdfUri: String): Int {
        val cacheFile = File(context.cacheDir, "lastPages.txt")
        if (!cacheFile.exists()) {
            cacheFile.createNewFile()
        }

        val lastPagesMap = mutableMapOf<String, Int>()

        // Read the file and fill the map
        BufferedReader(FileReader(cacheFile)).use { reader ->
            reader.forEachLine { line ->
                val parts = line.split(" - ")
                if (parts.size > 1) {
                    val name = parts.subList(0, parts.size - 1).joinToString(" - ")
                    lastPagesMap[name] = parts[parts.size - 1].toInt()
                }
            }
        }

        // Get the last page or default to 0
        val lastPage = lastPagesMap[pdfUri] ?: 0

        // If it's a new URI, add it to the map and write to the file
        if (!lastPagesMap.containsKey(pdfUri)) {
            lastPagesMap[pdfUri] = lastPage
            FileWriter(cacheFile, true).use { writer ->
                writer.append("$pdfUri - $lastPage\n")
            }
        }

        return lastPage
    }


    private fun getPdfMetadata(context: Context, pdfFile: Uri): PdfDocument.Meta {
        val pdfiumCore = PdfiumCore(context)
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(pdfFile, "r")
        val pdfDocument = pdfiumCore.newDocument(parcelFileDescriptor)

        // Get metadata
        val meta = pdfiumCore.getDocumentMeta(pdfDocument)

        // Close the PdfiumCore and the document
        pdfiumCore.closeDocument(pdfDocument)

        return meta
    }


    //TODO: optimize it
    private fun extractFirstPageAsJpg(context: Context, pdfFile: Uri): File {
        val pdfiumCore = PdfiumCore(context)
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(pdfFile, "r")
        val pdfDocument = pdfiumCore.newDocument(parcelFileDescriptor)

        val pageNumber = 0 // First page
        pdfiumCore.openPage(pdfDocument, pageNumber)

        val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
        val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNumber, 0, 0, width, height)

        // Save Bitmap as JPG to app's cache directory
        val jpgFile = File(context.cacheDir, "first_page.jpg")
        FileOutputStream(jpgFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
        }

        // Close the PdfiumCore and the document
        pdfiumCore.closeDocument(pdfDocument)

        return jpgFile
    }
}