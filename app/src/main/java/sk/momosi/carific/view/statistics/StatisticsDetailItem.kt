package sk.momosi.carific.view.statistics

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import sk.momosi.carific.R
import java.math.BigDecimal
import java.text.DecimalFormat

class StatisticsDetailItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : LinearLayout(context, attrs, R.attr.statisticsItemDetail) {

    private val nameView: TextView
    private val valueView: TextView
    private val unitView: TextView

    private var name: String? = null
    private var value: String? = null
    private var unit: String? = null

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.StatisticsDetailItem, 0, 0)
        name = a.getString(R.styleable.StatisticsDetailItem_name)
        value = a.getString(R.styleable.StatisticsDetailItem_value)
        unit = a.getString(R.styleable.StatisticsDetailItem_unit)

        a.recycle()

        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_statistics_detail_item, this, true)

        nameView = getChildAt(0) as TextView
        nameView.text = name

        valueView = getChildAt(1) as TextView
        valueView.text = value

        unitView = getChildAt(2) as TextView
        unitView.text = unit
    }

    fun setName(name: String) {
        this.name = name
        nameView.text = name
    }

    fun setValue(value: String?) {
        this.value = value
        valueView.text = value
    }

    fun setValue(value: BigDecimal?) {
        this.value = DecimalFormat.getInstance().format(value ?: 0)
        valueView.text = this.value
    }

    fun setValue(value: Number?) {
        this.value = value.toString()
        valueView.text = this.value
    }

    fun setUnit(unit: String) {
        this.unit = unit
        unitView.text = unit
    }
}
