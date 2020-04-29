package com.example.commandlinetime

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_plot.*
import java.io.File

class Plot : AppCompatActivity() {
    val util = Utility()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plot)

        // get plot command
        val bundle = intent.extras
        val fullCommands = bundle!!.getString("cmd")

        // set scrollable text view
        textViewPlotDetail.movementMethod = ScrollingMovementMethod()

        // plot
        plotPieChart(fullCommands!!)
    }

    private fun plotPieChart(fcmd: String) {
        val colors = mutableListOf<Int>(
            this.getColor(R.color.lt_orange),
            this.getColor(R.color.lt_pink),
            this.getColor(R.color.lt_purple),
            this.getColor(R.color.lt_yellow),
            this.getColor(R.color.Green_Snake),
            this.getColor(R.color.Blue_Diamond),
            this.getColor(R.color.Firebrick),
            this.getColor(R.color.Purple_Dragon),
            this.getColor(R.color.Halloween_Orange),
            this.getColor(R.color.Carbon_Gray)
        )
        val cmds = fcmd.split('\n')
        val activities = cmds.subList(1, cmds.size)
        val timeUsage = mutableMapOf<String, Long>()

        // initialize counter
        for (activity in activities)
            if (!activity.isEmpty())
                timeUsage[activity] = 0

        // open time.txt file and count the time for each activity
        val timeFile = File(filesDir, "time.txt")
        val lines = timeFile.readLines()
        for (line in lines) {
            val time = util.getTimeInSecByLine(line)
            for (activity in activities) {
                if (line.contains(activity)) {
                    timeUsage[activity]?.let { timeUsage.put(activity, it + time)}
                }
            }
        }

        // prepare pie chart data, text view content (details) and plot
        var details: String = "Details:\n"
        val entries = arrayListOf<PieEntry>()
        for (i in timeUsage) {
            // update detail time count
            val h = i.value.toLong() / 3600
            val m = i.value.toLong() % 3600 / 60
            val s = i.value.toLong() % 3600 % 60
            details += "\'${i.key}\'   "
            if (h > 0) details += "$h h "
            if (m > 0) details += "$m m "
            if (s > 0) details += "$s s "
            details += if (i.value == 0L) "none\n" else "\n"

            // update pie chart entry data (only if it is greater than zero, Don't plot zero term
            if (i.value > 0)
                entries.add(PieEntry(i.value.toFloat(), i.key))
        }
        val set = PieDataSet(entries, "Activity time")
//        set.setColors(ColorTemplate.JOYFUL_COLORS.toMutableList())
        set.colors = colors
        set.sliceSpace = 3f
        val pieData = PieData(set)

        // styling pieData
        pieData.setValueTextSize(12f)

        // styling pie chart view
        pieChartView.data = pieData
        pieChartView.setUsePercentValues(true)
        pieChartView.description.isEnabled = false
        pieChartView.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChartView.dragDecelerationFrictionCoef = 0.95f
        pieChartView.transparentCircleRadius = 60f
        pieChartView.isDrawHoleEnabled = true
        pieChartView.animateY(1000, Easing.EaseInOutCubic)
        pieChartView.invalidate()

        // update text view
        textViewPlotDetail.text = details
    }

}