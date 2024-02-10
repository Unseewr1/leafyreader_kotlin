package com.unseewr1.leafyreader.book

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import com.unseewr1.leafyreader.PdfViewerActivity
import com.unseewr1.leafyreader.R
import java.io.File
import java.io.FileOutputStream

class BookAdapter(
    private val context: Context,
    private val items: List<Book>,
) : RecyclerView.Adapter<BookViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BookViewHolder =
        BookViewHolder(LayoutInflater.from(context).inflate(R.layout.book_view, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: BookViewHolder,
        position: Int,
    ) {
        val meta = getPdfMetadata(context, items[position].uri)
        holder.bookTitleView.text = meta.title
        holder.bookAuthorView.text = meta.author
        val startTime = System.currentTimeMillis()
        holder.bookPreviewView.setImageBitmap(BitmapFactory.decodeFile(extractFirstPageAsJpg(context, items[position].uri).path))
        Log.i("speed", "${System.currentTimeMillis() - startTime}ms")

        holder.bookView.setOnClickListener {
            val intent = Intent(context, PdfViewerActivity::class.java)
            intent.putExtra("pdfUri", items[position].uri)
            context.startActivity(intent)
        }
    }


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
}