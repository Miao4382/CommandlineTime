package com.example.commandlinetime

import java.io.File
import java.lang.Character.isLetter

class Utility {
  /* getCommandType()
  * this function will parse the input string. If it doesn't start with '/', the input should be
  * printed to time.txt directly (return "directWrite"). Otherwise, it will extract the command
  * after '/' and return it
  *
  * Return value:
  *   "directWrite"
  *   "start"
  *   "end"
  *   "record"
  *   "plot"
  *   "showraw"
  *   "showtemp" (will show the temp file used for tracking activities)
  *   "delete"
  * */
  fun getCommandType(s: String): String {
    // check if the first character is '/'
    if (s.isEmpty() || s[0] != '/')
      return (if (s.isEmpty())  "empty" else "directWrite")
    // extract the command
    // find out the index of first character not alpha
    var endIndex = 1
    while (endIndex < s.length && isLetter(s[endIndex])) {endIndex++}
    return s.substring(1, endIndex)
  }

  /* getActivity()
  * this function will extract the activity from user's command "/start activity" and return it
  * Assumption: only one line of activity! If input has more than one line, only line 1 will be
  * recorded
  * */
  fun getActivity(s: String): String {
    val index = s.indexOf(' ')
    val indexLF = s.indexOf('\n')

    if (index == -1 || index == s.lastIndex) {
      return "NOACTIVITY"
    } else {
      return (if (indexLF != -1) s.substring(index + 1, indexLF) else s.substring(index + 1))
    }
  }

  /* randomQuote()
  * Will pick a random quote
  * */
  fun randomQuote(): String {
    return "Status: idle\nTime is money, my friend!"
  }

  /* checkPlotSyntax()
  * cmd: the /plot command typed in the text box.
  * This function will check if the syntax of /plot command is correct.
  * "/plot" command must have at least two lines, the first line is "/plot"
  * Following line(s) will contain the specific activity user wants to search and plot. Each line
  * is actually a keyword to be searched in time.txt, if there is a match (in time-recording line,
  * which is a line that matches the format:  hh:mm:ss-hh:mm:ss < aah bbm ccs > activity
  * Thus, each line in plot command can't be empty (except the last line)
  *
  * return true: syntax matches
  * return false: syntax doesn't match
  * */
  fun checkPlotSyntax(fullCommand: String): Boolean {
    val cmds = fullCommand.split('\n')
    for (i in cmds.indices) {
      if (cmds[i].isEmpty() && (i != cmds.size - 1 || i == 1))
        return false
    }
    return (cmds.size != 1)
  }

  /* isTimeRecordLine()
  * Will check if line match the format of time recording recorded by /start, /end command
  * Format should be like: hh:mm:ss-hh:mm:ss < aah bbm ccs > activity
  *
  * Use simple format checking:
  * ':' should be present at index 2, 5, 11, 14
  * '~' should be present at index 8
  * '<' should be present at index 18
  * */
  fun isTimeRecordLine(line: String): Boolean {
    return line.length >= 22 && line[2] == ':' && line[5] == ':' && line[11] == ':' && line[14] == ':' && line[8] == '~' && line[18] == '<'
  }

  /* getTimeInSecByLine()
  * Will extract the time encoded in the time record line
  * If the line is not a valid time record line, return 0
  * */
  fun getTimeInSecByLine(line: String): Long {
    if (!isTimeRecordLine(line))
      return 0
    // get string between < >, which encodes the time of this activity
    val time = line.substring(20, line.indexOf('>') - 1)
    var h: Long = 0
    var m: Long = 0
    var s: Long = 0
    if (time.contains('h'))
      h = time.substring(time.indexOf('h') - 2, time.indexOf('h')).toLong()
    if (time.contains('m'))
      m = time.substring(time.indexOf('m') - 2, time.indexOf('m')).toLong()
    if (time.contains('s'))
      s = time.substring(time.indexOf('s') - 2, time.indexOf('s')).toLong()
    return h * 3600 + m * 60 + s
  }
}