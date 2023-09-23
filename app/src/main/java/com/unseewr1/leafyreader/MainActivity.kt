package com.unseewr1.leafyreader

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var topPanel: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        topPanel = findViewById(R.id.topPanel)

        topPanel.setOnClickListener {

            topPanel.text = "${topPanel.text?:""}lorem"
            Log.i(null, topPanel.text.toString())
        }
    }
}