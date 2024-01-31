package com.unseewr1.leafyreader

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnTapListener
import java.io.File

class PdfViewerActivity : AppCompatActivity() {
    private lateinit var pdfView: PDFView

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        pdfView = findViewById(R.id.pdfView) // Replace with your PDFView ID
        pdfView.setBackgroundColor(Color.CYAN)

        // Get the file Uri from the intent or another source
        val fileUri: Uri = intent.getParcelableExtra("pdfUri")!! // Replace with your Uri

        displayPdf(fileUri)
    }


    private fun displayPdf(uri: Uri) {
        // Convert Uri to File
        val pdfFile = File(uri.path!!)

        // Load and display the PDF
        pdfView.fromFile(pdfFile)
//            .enableSwipe(true)
//            .swipeHorizontal(false)
            .onPageScroll { page, positionOffset -> // This method will be called during page scrolling
                Log.i("tag", "$page $positionOffset")
            }
            .onTap(CustomOnTapListener(pdfView, pdfFile, resources))
            .defaultPage(0) // Display the first page
            .pages(0)
            .load()
    }
}

class CustomOnTapListener(
    private val pdfView: PDFView,
    private val pdfFile: File,
    private val resources: Resources,
) : OnTapListener {

    private var nextPage = 0

    override fun onTap(e: MotionEvent?): Boolean {
        if (e != null) {
            // Determine if the tap is on the left or right side of the screen
            val tapX = e.x
            val screenWidth = resources.displayMetrics.widthPixels

            // Calculate the threshold for determining left/right side (e.g., 50% of the screen)
            val threshold = screenWidth / 2

            // Determine if the tap is on the left or right side based on the threshold
            val isOnRightSide = tapX > threshold

            // Update the next page based on the tap side
            if (isOnRightSide) {
                nextPage++
            } else {
                nextPage--
                if (nextPage < 0) nextPage = 0
            }

            // Load and display the specified page
            pdfView.fromFile(pdfFile)
                .onPageScroll { page, positionOffset ->
                    // This method will be called during page scrolling
                    Log.i("tag", "$page $positionOffset")
                }
                .onTap(this)
                .defaultPage(nextPage)
                .pages(nextPage)
                .load()

            Log.i("tap", "$e")
        }

        return true
    }
}