package com.example.commandlinetime

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
}