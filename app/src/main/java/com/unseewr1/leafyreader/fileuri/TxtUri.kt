package com.unseewr1.leafyreader.fileuri

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
        return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    }

    override fun getOnClickListener() = View.OnClickListener {
        val intent = Intent(context, TxtViewerActivity::class.java)
        intent.putExtra("txtUri", asUri())
        context.startActivity(intent)
    }
}
