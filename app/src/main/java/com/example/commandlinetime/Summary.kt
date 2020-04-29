package com.example.commandlinetime

import android.os.Bundle
import android.os.PersistableBundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_summary.*
import java.io.File

class Summary : AppCompatActivity() {
  val util = Utility()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_summary)

    // get summary command
    val bundle = intent.extras
    val fullCommands = bundle!!.getString("cmd")

    // set scrollable text view
    textViewSummary.movementMethod = ScrollingMovementMethod()

    // generate summary
    generateSummary(fullCommands!!)
  }

  private fun generateSummary(fcmd: String) {
    val cmds = fcmd.split('\n')
    val activities = cmds.subList(1, cmds.size)
    var summary = ""

    // open time file and read each line
    val timeFile = File(filesDir, "time.txt")
    val lines = timeFile.readLines()
    for (line in lines) {
      // print date title if encountered one
      if (util.isDateTitleLine(line)) {
        summary += line + "\n"
        continue
      }

      // add line to summary if activity matches
      for (activity in activities) {
        if (line.contains(activity) && util.isTimeRecordLine(line)) {
          summary += line + "\n"
        }
      }
    }

    // set text view
    textViewSummary.text = summary
  }

}