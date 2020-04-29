package com.example.commandlinetime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_view_text.*
import java.io.File

class ViewTextTemp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_text_temp)

        // try to open time.txt to display its content
        val timeFile = File(filesDir, "temp.txt")

        // check if file can read or not
        if (timeFile.canRead()) {
            val text = timeFile.readText()
            textView.text = if (text.isEmpty()) "Temp file empty" else text
        } else {
            textView.text = "No temp file found.\n"
        }
    }


}