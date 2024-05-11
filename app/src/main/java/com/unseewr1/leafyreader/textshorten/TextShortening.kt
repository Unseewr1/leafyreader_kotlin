package com.unseewr1.leafyreader.textshorten

import android.graphics.Paint
import android.os.Build
import android.util.Log
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView


fun shortenText(textView: TextView) {
    textView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayout.invoke(textView))
}


private fun shortenTextDefault(textView: TextView) {
    val startTime = System.nanoTime()
    val originalText: CharSequence = textView.text
    val paint: Paint = textView.paint
    var width = paint.measureText(originalText, 0, originalText.length)

    if (width > textView.width) {
        var maxLength = originalText.length
        val ellipsisWidth = paint.measureText("...")
        while (width + ellipsisWidth > textView.width) {
            maxLength--
            width = paint.measureText(originalText, 0, maxLength)
        }
        val shortenedText: CharSequence =
            originalText.subSequence(0, maxLength).toString() + "..."
        textView.text = shortenedText
    } else {
        textView.text = originalText
    }
    Log.i("speedNanos", "${System.nanoTime() - startTime}nanos")
}

private val onGlobalLayout =
    when (Build.VERSION.SDK_INT) {
        in 1..<Build.VERSION_CODES.JELLY_BEAN -> { textView: TextView ->
            OnGlobalLayoutListener { shortenTextDefault(textView) }
        }

        else -> { textView: TextView ->
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    shortenTextDefault(textView)
                }
            }
        }
    }
