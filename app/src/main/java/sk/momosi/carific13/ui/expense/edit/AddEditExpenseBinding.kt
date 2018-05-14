package sk.momosi.carific13.ui.expense.edit

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.support.v7.widget.AppCompatEditText
import android.widget.TextView
import sk.momosi.carific13.util.DateUtils
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*


/**
 * @author Lenka Heldová
 * @date 02.04.2018.
 */
object AddEditExpenseBinding {

    @JvmStatic
    @BindingAdapter("android:text")
    fun bindIntegerInText(tv: AppCompatEditText, value: Int?) {

        val displayed = if (tv.text.isNullOrBlank()) {
            null
        } else {
            Integer.valueOf(tv.text.toString())
        }

        if (value != null && displayed != value)
            tv.setText(value.toString())
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text")
    fun getIntegerFromBinding(view: TextView): Int? {
        if (view.text.isNullOrBlank())
            return null

        return Integer.parseInt(view.text.toString())
    }

    @JvmStatic
    @BindingAdapter("android:text")
    fun bindBigDecimalInText(tv: AppCompatEditText, value: BigDecimal?) {
        val displayed = if (tv.text.isNullOrBlank()) {
            null
        } else {
            val text = tv.text.replace(Regex("[^0-9]"),"")
            BigDecimal(text)
        }

        if (value != null && value != displayed) {
            tv.setText(value.toString())
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text")
    fun getBigDecimalFromBinding(view: TextView): BigDecimal? {
        return if (view.text.isNullOrBlank()) null else {
            val text = view.text.replace(Regex("[^0-9]"),"")
            BigDecimal(text)
        }
    }

    @JvmStatic
    @BindingAdapter("android:text")
    fun bindBigDecimalInTextView(tv: TextView, value: BigDecimal?) {
        val format = DecimalFormat.getInstance()

        val displayed = if (tv.text.isNullOrBlank()) {
            null
        } else {
            BigDecimal(tv.text.replace(Regex("[^0-9]"),""))
        }

        if (value != null && value != displayed) {
            tv.text = format.format(value)
        }
    }

    @JvmStatic
    @BindingAdapter("date")
    fun bindDate(tv: TextView, value: Calendar) {
        tv.text = DateUtils.localizeDate(value.time, tv.context)
    }

    @JvmStatic
    @BindingAdapter("time")
    fun bindTime(tv: TextView, value: Calendar) {
        tv.text = DateUtils.localizeTime(value, tv.context)
    }
}