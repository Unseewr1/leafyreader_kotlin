package com.unseewr1.leafyreader

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unseewr1.leafyreader.book.Book
import com.unseewr1.leafyreader.book.BookAdapter
import com.unseewr1.leafyreader.permission.externalstoragemanagement.ExternalStorageManagementPermissionGranderFactory
import java.io.File


class MainActivity : AppCompatActivity() {

    private val permissionGrander = ExternalStorageManagementPermissionGranderFactory.create(this)


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)

        val uris = getSupportedFileUris(this)
        val books = uris
            .map { Book(it) }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BookAdapter(this, books)



        permissionGrander.requirePermission()

        /*topPanel.setOnClickListener {

            try {

                val timeInMs = System.currentTimeMillis()
                val items = getSupportedFileUris(this)
                    .map { Item(R.drawable.ic_launcher_foreground, it) }
                    .toList()
                topPanel.text = "${System.currentTimeMillis() - timeInMs}ms"

                if (items.isEmpty()) {
                    val alertDialogBuilder = AlertDialog.Builder(this)

                    // Set the title and message for the dialog
                    alertDialogBuilder.setTitle("Alert Title")
                    alertDialogBuilder.setMessage("This is a simple alert message.")

                    // Set positive button and its click listener
                    alertDialogBuilder.setPositiveButton("OK") { dialog, which ->
                        // Do something when the user clicks OK
                        dialog.dismiss() // Close the dialog
                    }

                    // Set negative button and its click listener (optional)
                    alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
                        // Do something when the user clicks Cancel (optional)
                        dialog.dismiss() // Close the dialog
                    }

                    // Create and show the dialog
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                }
                val objects = items
                val customAdapter = CustomAdapter(this, R.id.bookGrid, objects)
                bookGrid.adapter = customAdapter


                // Notify the adapter of data changes
                customAdapter.notifyDataSetChanged()
            } catch (e: Throwable) {
                topPanel.text = e.message
            }
        }*/
    }


    fun Search_Dir(dir: File): List<File> {
        val pdfPattern = ".pdf"
        val FileList = dir.listFiles()
        val result = mutableListOf<File>()
        if (FileList != null) {
            for (i in FileList.indices) {
                if (FileList[i].isDirectory) {
                    result.addAll(Search_Dir(FileList[i]))
                } else {
                    if (FileList[i].name.endsWith(pdfPattern)) {
                        result.add(FileList[i])
                    }
                }
            }
        }
        return result
    }

    @RequiresApi(Build.VERSION_CODES.Q)
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
            }

            // Close the cursor when done
            cursor?.close()
        }

        return allVolumePdfFiles.toList()
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