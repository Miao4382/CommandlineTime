package com.example.commandlinetime

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_plot.*

class Plot : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plot)

        // get plot command
        val bundle = intent.extras
        val cmd = bundle!!.getString("cmd")

        textView.text = cmd
    }


}