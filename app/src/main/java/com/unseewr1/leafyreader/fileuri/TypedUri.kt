package com.unseewr1.leafyreader.fileuri

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import java.io.File

abstract class TypedUri(
    protected val context: Context,
    protected val uri: Uri,
) {

    abstract fun title(): String
    abstract fun author(): String
    abstract fun cover(): Bitmap
    abstract fun getOnClickListener(): View.OnClickListener
    fun asUri() = uri

    companion object {

        fun fromFile(
            context: Context,
            file: File
        ): TypedUri? = when (file.extension.lowercase()) {
            "pdf" -> PdfUri(context, file)
            "doc" -> DocUri(context, file)
            "txt" -> TxtUri(context, file)
            else -> null
        }
    }
}