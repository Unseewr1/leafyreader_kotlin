package com.unseewr1.leafyreader.fileuri

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.core.net.toFile
import androidx.core.net.toUri
import org.apache.poi.hwpf.HWPFDocument
import java.io.File

class DocUri(context: Context, file: File) : TypedUri(context, file.toUri()) {

    override fun title(): String = uri.toFile().nameWithoutExtension

    override fun author(): String = ""

    //TODO: change to reading first of pages
    override fun cover(): Bitmap {
        return Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
    }

    override fun getOnClickListener() = View.OnClickListener {
        try {
            val contentResolver= context.contentResolver
            val docFile = contentResolver.openInputStream(uri)
            val doc = HWPFDocument(docFile)
//            val paragraphs = doc.paragraphs
            val content = StringBuilder()
//            for (paragraph in paragraphs) {
//                val text = paragraph.text()
//                content.append(text)
//            }
//            textView.text = content.toString()
        } catch (e: Exception) {
            e.printStackTrace()
//            textView.text = "Error reading file: ${e.message}"
        }
    }
}
