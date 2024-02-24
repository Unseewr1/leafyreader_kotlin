package com.unseewr1.leafyreader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unseewr1.leafyreader.book.Book
import com.unseewr1.leafyreader.book.BookAdapter
import com.unseewr1.leafyreader.permission.externalstoragemanagement.ExternalStorageManagementPermissionGranderFactory
import com.unseewr1.leafyreader.urifetcher.getSupportedFileUris


class MainActivity : AppCompatActivity() {

    private val permissionGrander = ExternalStorageManagementPermissionGranderFactory.create(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)

        permissionGrander.requirePermission()

        val uris = getSupportedFileUris(this)
        val books = uris
            .map { Book(it) }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BookAdapter(this, books)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionGrander.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}