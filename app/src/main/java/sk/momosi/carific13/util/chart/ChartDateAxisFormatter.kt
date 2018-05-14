package sk.momosi.carific13.util.chart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter


class ChartDateAxisFormatter(var dates: List<String>) : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        // "value" represents the position of the label on the axis (x or y)
        return if (value.toInt() >= 0 && value.toInt() < dates.size) dates[value.toInt()] else ""
    }
}