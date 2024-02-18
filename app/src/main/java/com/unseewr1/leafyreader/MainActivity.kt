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

    /*@RequiresApi(Build.VERSION_CODES.Q)
    fun getSupportedFileUris(context: Context): List<Uri> {
        val contentResolver: ContentResolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA
        )

        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
        val selectionArgs = arrayOf("application/pdf")

        val volumes = MediaStore.getExternalVolumeNames(context)

        val allVolumePdfFiles = mutableListOf<Uri>()
        for (volumeName in volumes) {
            // Create the content URI for the specific volume
            val volumeUri = MediaStore.Files.getContentUri(volumeName)

            // Perform the query to find PDF files in the volume
            val cursor = contentResolver.query(
                volumeUri,
                projection,
                selection,
                selectionArgs,
                null
            )

            cursor?.use {
                val dataColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                while (cursor.moveToNext()) {
                    val filePath = cursor.getString(dataColumnIndex)
                    allVolumePdfFiles.add(Uri.parse("file://$filePath"))
                }
                it.close()
            }
        }

        return allVolumePdfFiles.toList()
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionGrander.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}