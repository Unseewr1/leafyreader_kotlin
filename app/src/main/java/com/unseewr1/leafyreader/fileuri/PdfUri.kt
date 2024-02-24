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
import java.io.File
import java.io.FileOutputStream

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
        context.startActivity(intent)
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