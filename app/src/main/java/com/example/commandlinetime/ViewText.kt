package com.example.commandlinetime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_view_text.*
import java.io.File

class ViewText : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_text)

        // try to open time.txt to display its content
        val path = filesDir
        val timeFile = File(path, "time.txt")

        // check if file can read or not
        if (timeFile.canRead()) {
            val text = timeFile.readText()
            textView.text = if (text.isEmpty()) "It looks like you don't have any record yet." else text
        } else {
            textView.text = "No raw data file found.\n"
        }
    }


}