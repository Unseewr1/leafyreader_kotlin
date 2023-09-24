package com.unseewr1.leafyreader

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.TextView
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        topPanel.text = "${Build.VERSION.SDK_INT}"

        permissionGrander.requirePermission()

        topPanel.setOnClickListener {

            try {

                val timeInMs = System.currentTimeMillis()
                val items = Search_Dir(Environment.getExternalStorageDirectory())
                    .map { it.toString() }
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
                bookGrid.adapter = ArrayAdapter(this, R.layout.grid_item, R.id.itemText, items)
            } catch (e: Throwable) {
                topPanel.text = e.message
            }
        }
    }

    fun Search_Dir(dir: File): List<File> {
        val pdfPattern = ".pdf"
        val FileList = dir.listFiles()
        val result =  mutableListOf<File>()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionGrander.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}