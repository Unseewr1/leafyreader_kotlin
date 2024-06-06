package com.unseewr1.leafyreader

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnTapListener
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var pdfView: PDFView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        pdfView = findViewById(R.id.pdfView) // Replace with your PDFView ID
        pdfView.setBackgroundColor(Color.CYAN)

        // Get the file Uri from the intent or another source
        val fileUri: Uri = intent.getParcelableExtra("pdfUri")!! // Replace with your Uri

        displayPdf(fileUri, intent)
    }


    private fun displayPdf(uri: Uri, intent: Intent) {
        // Convert Uri to File
        val pdfFile = File(uri.path!!)

        val lastOpenedPage = getLastOpenedPage(intent)
        // Load and display the PDF
        pdfView.fromFile(pdfFile)
//            .enableSwipe(true)
//            .swipeHorizontal(false)
            .onPageScroll { page, positionOffset -> // This method will be called during page scrolling
                Log.i("tag", "$page $positionOffset")
            }
            .onTap(CustomOnTapListener(this, pdfView, pdfFile, resources, lastOpenedPage))
            .defaultPage(lastOpenedPage) // Display the first page
            .pages(lastOpenedPage)
            .load()
    }

    private fun getLastOpenedPage(intent: Intent): Int {
        return intent.getIntExtra("lastPage", 0)
    }
}

class CustomOnTapListener(
    private val context: Context,
    private val pdfView: PDFView,
    private val pdfFile: File,
    private val resources: Resources,
    private var nextPage: Int
) : OnTapListener {

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

            setLastPage(context, pdfFile.path.toString(), nextPage)

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

    private fun setLastPage(context: Context, pdfUri: String, lastPage: Int) {
        val cacheFile = File(context.cacheDir, "lastPages.txt")
        if (!cacheFile.exists()) {
            cacheFile.createNewFile()
        }

        val lastPagesMap = mutableMapOf<String, Int>()

        // Read the file and fill the map
        BufferedReader(FileReader(cacheFile))
            .lines()
            .forEach { line ->
            val parts = line.split(" - ")
                if (parts.size > 1) {
                    val name = parts.subList(0, parts.size - 1).joinToString(" - ")
                    lastPagesMap[name] = parts[parts.size - 1].toInt()
                }
        }

        lastPagesMap[pdfUri] = lastPage
        FileWriter(cacheFile).use { writer ->
            lastPagesMap.forEach {
                writer.append("${it.key} - ${it.value}\n")
            }
        }
    }
}