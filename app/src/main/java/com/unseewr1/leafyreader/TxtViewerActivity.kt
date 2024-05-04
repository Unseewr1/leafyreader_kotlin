package com.unseewr1.leafyreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class TxtViewerActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_txt_file)

        val textView = findViewById<TextView>(R.id.textView)

        // Get the intent that started this activity
        val intent: Intent? = intent
        val uri: Uri = intent?.getParcelableExtra("txtUri")!! // Replace with your Uri

        uri.let {
            try {
                // Open the URI for reading
                contentResolver.openInputStream(it)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?
                    // Read lines from the file and append to the StringBuilder
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line).append("\n")
                    }
                    // Set the text of the TextView to the contents of the file
                    textView.text = stringBuilder.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } ?: run {
            textView.text = "No file selected"
        }
    }
}