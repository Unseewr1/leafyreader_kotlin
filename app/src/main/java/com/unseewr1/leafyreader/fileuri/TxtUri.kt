package com.unseewr1.leafyreader.fileuri

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.unseewr1.leafyreader.TxtViewerActivity
import java.io.File


class TxtUri(
    context: Context,
    file: File,
) : TypedUri(context, file.toUri()) {

    override fun title(): String = asUri().toFile().nameWithoutExtension

    override fun author(): String = ""

    override fun cover(): Bitmap {
        // Create a Bitmap with a dark blue background
        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.parseColor("#446391")) // Set dark blue background color

        // Create Paint object for drawing text
        val paint = Paint()
        paint.color = Color.WHITE // Set text color to white

        paint.textSize = 480f // Set text size for the letter

        // Calculate text position to center it on the bitmap
        val text = if (title().isEmpty()) "TXT" else title().substring(0, 1)
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        val x = (bitmap.width - textBounds.width()) / 2f
        val y = (bitmap.height + textBounds.height()) / 2f

        // Draw text on the Bitmap
        canvas.drawText(text, x, y, paint)

        return bitmap
    }

    override fun getOnClickListener() = View.OnClickListener {
        val intent = Intent(context, TxtViewerActivity::class.java)
        intent.putExtra("txtUri", asUri())
        context.startActivity(intent)
    }
}
