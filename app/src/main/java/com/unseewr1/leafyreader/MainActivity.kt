package com.unseewr1.leafyreader

import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.unseewr1.leafyreader.permission.externalstoragemanagement.ExternalStorageManagementPermissionGranderFactory
import java.io.File


class MainActivity : AppCompatActivity() {

    private val permissionGrander = ExternalStorageManagementPermissionGranderFactory.create(this)


    private val topPanel: TextView by lazy {
        findViewById(R.id.topPanel)
    }

    private val bookGrid: GridView by lazy {
        findViewById(R.id.bookGrid)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        topPanel.text = "${Build.VERSION.SDK_INT}"

        permissionGrander.requirePermission()

        topPanel.setOnClickListener {

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
        }
    }

    inner class CustomAdapter(context: Context, id: Int, objects: List<Item>) :
        ArrayAdapter<Item>(context, id, objects) {

        override fun getItem(position: Int): Item = super.getItem(position)!!

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val cover = LinearLayout(this@MainActivity)
            val imageView = ImageView(applicationContext)
            val textView = TextView(cover.context)
            getItem(position).let {
                imageView.setImageResource(it.imageResource)
                textView.text = it.uri.toString()
            }
            cover.addView(imageView)
            cover.addView(textView)
            cover.setOnClickListener {
                val intent = Intent(Intent.ACTION_MAIN)
                    .let {
                        it.component = ComponentName("com.unseewr1.leafyreader", "com.unseewr1.leafyreader.PdfViewerActivity")
                        it.putExtra("pdfUri", getItem(position).uri)
                    }
                startActivity(intent)
            }
            return cover
        }
    }

    data class Item(val imageResource: Int, val uri: Uri)

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