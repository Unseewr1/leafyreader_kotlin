/*
package com.unseewr1.leafyreader

import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView // You may use a PDF rendering library

class PdfViewerActivity : AppCompatActivity() {
    private lateinit var pdfView: PDFView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        pdfView = findViewById(R.id.pdfView) // Replace with your PDFView ID

        // Get the file Uri from the intent or another source
        val fileUri = intent.data // Replace with your Uri

        try {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                contentResolver.openFileDescriptor(fileUri!!, "r")

            if (parcelFileDescriptor != null) {
                val pdfRenderer = PdfRenderer(parcelFileDescriptor)
                val pageCount = pdfRenderer.pageCount

                // Display the first page of the PDF
                if (pageCount > 0) {
                    val page = pdfRenderer.openPage(0)
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    pdfView.fromBitmap(bitmap).load()
                    page.close()
                }

                pdfRenderer.close()
                parcelFileDescriptor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any exceptions that may occur during rendering
        }
    }
}*/
