package com.example.commandlinetime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {
  val MAX_ACTIVITY_INTERVAL = 86399   // only record activity less than this amount of seconds
  val MIN_ACTIVITY_INTERVAL = 2       // only record activity longer than this amount of seconds
  val WEEKDAYS = listOf<String>("Sun.", "Mon.", "Tue.", "Wed.", "Thur.", "Fri.", "Sat.")
  val util = Utility()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // set listener for commit button
    btnCommit.setOnClickListener { parseCommand() }

    // create time.txt file if none
    val timeFile = File(filesDir, "time.txt")
    if (!timeFile.exists()) {
      val fout = FileOutputStream(timeFile)
      fout.write("".toByteArray())
    }

    // update status text view based on temp.txt
    updateStatusTextByTempFile()

  }

  private fun parseCommand() {

    val cmd = util.getCommandType(editText.text.toString())
//    Toast.makeText(this, cmd, Toast.LENGTH_SHORT).show()

    if (cmd == "showraw") {
      val intent = Intent(this, ViewText::class.java)
      startActivity(intent)
    }

    if (cmd == "showtemp") {
      val intent = Intent(this, ViewTextTemp::class.java)
      startActivity(intent)
    }

    if (cmd == "directWrite") {
      val timeFile = File(filesDir, "time.txt")
      if (timeFile.canWrite()) {
        val fout = FileOutputStream(timeFile, true)
        fout.write(editText.text.toString().toByteArray())
        fout.write("\n".toByteArray())
      } else {
        Toast.makeText(this, "Error: can't write to time.txt", Toast.LENGTH_SHORT).show()
      }
    }

    if (cmd == "delete") {
      val timeFile = File(filesDir, "time.txt")
      if (timeFile.exists()) {
        timeFile.delete()
        Toast.makeText(this, "time.txt file deleted!", Toast.LENGTH_SHORT).show()
      }

    }

    if (cmd == "init") {
      val timeFile = File(filesDir, "time.txt")
      val fout = FileOutputStream(timeFile)
    }

    if (cmd == "start") {
      startRecording()
    }

    if (cmd == "end") {
      endRecording()
      updateStatusTextByTempFile()
    }

    if (cmd == "plot") {
      // check if the command is matching the syntax
      val fullCommand = editText.text.toString()

      if (!util.checkPlotSyntax(fullCommand))
        Toast.makeText(this, "Syntax error!", Toast.LENGTH_SHORT).show()
      else if (!isFileExist("time.txt")) {
        Toast.makeText(this, "No record file found!", Toast.LENGTH_SHORT).show()
      }
      else {
        // start Plot activity and pass the full command to it
        val intent = Intent(this, Plot::class.java)
        intent.putExtra("cmd", fullCommand)   // pass command to Plot activity
        startActivity(intent)
      }
    }

    // reset the edit text view
    editText.text.clear()
  }

  private fun startRecording(): Boolean {
    // check if temp.txt exist. If so, it means the app is currently recording an activity
    val tempFile = File(filesDir, "temp.txt")

    if (tempFile.exists()) {
      Toast.makeText(this, "Please finish your current activity first", Toast.LENGTH_SHORT).show()
      return false
    }


    // check if activity was provided
    val currentAct = util.getActivity(editText.text.toString())
    if (currentAct == "NOACTIVITY") {
      Toast.makeText(this, "You should provide an activity", Toast.LENGTH_SHORT).show()
      return false
    }

    // create a temp file recording the status of activity being started
    val fout = FileOutputStream(tempFile)
    val c = Calendar.getInstance()
    val year = String.format("%04d", c.get(Calendar.YEAR))
    val month = String.format("%02d", c.get(Calendar.MONTH))    // month start with 0
    val day = String.format("%02d", c.get(Calendar.DAY_OF_MONTH))
    val hour = String.format("%02d", c.get(Calendar.HOUR_OF_DAY))
    val minute = String.format("%02d", c.get(Calendar.MINUTE))
    val second = String.format("%02d", c.get(Calendar.SECOND))

    // day_of_week start with 1: Sunday
    val date = "${String.format("%02d", c.get(Calendar.MONTH) + 1)}/$day/$year ${WEEKDAYS[c.get(Calendar.DAY_OF_WEEK) - 1]}"

    fout.write((currentAct + "\n").toByteArray())
    fout.write((year + "\n").toByteArray())
    fout.write((month + "\n").toByteArray())
    fout.write((day + "\n").toByteArray())
    fout.write((hour + "\n").toByteArray())
    fout.write((minute + "\n").toByteArray())
    fout.write((second + "\n").toByteArray())
    fout.write((date + "\n").toByteArray())

    // update text view
    textViewStatus.text = "Working on: " + currentAct
    Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show()

    return true
  }

  /* endRecording()
  * This function will check if temp.txt exist. If so, it will parse its content and update time.txt
  * If not, will show error message
  * */
  private fun endRecording(): Boolean {
    val tempFile = File(filesDir, "temp.txt")
    if (!tempFile.exists()) {
      Toast.makeText(this, "Currently idle!", Toast.LENGTH_SHORT).show()
      return false
    }

    // read from the temp file, also get current time
    // assumption1: length of one activity will be less than MAX_ACTIVITY_INTERVAL
    // assumption2: only record activity longer than MIN_ACTIVITY_INTERVAL
    val startTime = tempFile.readLines()
    val c = Calendar.getInstance()
    val hour = String.format("%02d", c.get(Calendar.HOUR_OF_DAY))
    val minute = String.format("%02d", c.get(Calendar.MINUTE))
    val second = String.format("%02d", c.get(Calendar.SECOND))

    // check time requirement
    var startC = Calendar.getInstance()
    startC.set(startTime[1].toInt(), startTime[2].toInt(), startTime[3].toInt(), startTime[4].toInt(), startTime[5].toInt(), startTime[6].toInt())

    val diffInSec = (ceil((c.timeInMillis - startC.timeInMillis).toDouble() / 1000)).toInt()
    val hourElapsed = diffInSec / 3600
    val minElapsed = (diffInSec % 3600) / 60
    val secElapsed = (diffInSec % 3600) % 60

    if (diffInSec > MAX_ACTIVITY_INTERVAL) {
      Toast.makeText(this, "Only support activity under 24 h", Toast.LENGTH_SHORT).show()
      tempFile.delete()
      return false
    } else if (diffInSec < MIN_ACTIVITY_INTERVAL) {
      Toast.makeText(this, "Activity should be longer than $MIN_ACTIVITY_INTERVAL seconds", Toast.LENGTH_SHORT).show()
      tempFile.delete()
      return false
    }

    // write this activity to time.txt
    val timeFile = File(filesDir, "time.txt")
    val fout = FileOutputStream(timeFile, true)
    val contents = timeFile.readText()

    // check if the current date is in the time.txt, if not, we add it, as it represents the beginning of one day
    if (!contents.contains(startTime[7]))
      fout.write(("\n\n${startTime[7]}\n").toByteArray())

    // write the specific line of this activity to time.txt
    fout.write(("${startTime[4]}:${startTime[5]}:${startTime[6]}").toByteArray())   // start time hh:mm:ss
    fout.write("~".toByteArray())   // delimiter
    fout.write(("$hour:$minute:$second").toByteArray())   // end time hh:mm:ss
    fout.write((" < ").toByteArray())
    if (hourElapsed > 0)
      fout.write(("${String.format("%02d", hourElapsed)}h ").toByteArray())
    if (minElapsed > 0)
      fout.write(("${String.format("%02d", minElapsed)}m ").toByteArray())
    if (secElapsed > 0)
      fout.write(("${String.format("%02d", secElapsed)}s ").toByteArray())
    fout.write(("> ").toByteArray())
    fout.write((startTime[0] + "\n").toByteArray())   // write activity details

    // delete temp file
    tempFile.delete()

    // reset status text view and Toast notice
    textViewStatus.text = util.randomQuote()
    Toast.makeText(this, "Finished", Toast.LENGTH_SHORT).show()

    return true
  }

  /* updateStatusTextByTempFile()
  * Will update the text box showing the current status
  * */
  private fun updateStatusTextByTempFile() {
    val tempFile = File(filesDir, "temp.txt")
    if (tempFile.exists()) {
      val tempInfo = tempFile.readLines()
      textViewStatus.text = "Working on: " + tempInfo[0]
    } else {
      textViewStatus.text = "Status: idle"
    }
  }

  private fun isFileExist(filename: String): Boolean {
    return File(filesDir, filename).exists()
  }
}
