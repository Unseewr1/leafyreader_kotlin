package com.unseewr1.leafyreader.book

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unseewr1.leafyreader.R

class BookViewHolder(
    bookView: View
) : RecyclerView.ViewHolder(bookView) {

    val bookView: RelativeLayout = bookView.findViewById(R.id.bookView)
    val bookPreviewView: ImageView = bookView.findViewById(R.id.bookPreviewView)
    val bookTitleView: TextView = bookView.findViewById(R.id.bookNameView)
    val bookAuthorView: TextView = bookView.findViewById(R.id.bookAuthorView)
}