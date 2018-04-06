package sk.momosi.carific.ui.fuel.edit

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.support.v7.widget.AppCompatEditText
import android.widget.TextView
import sk.momosi.carific.util.DateUtils
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*


/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
object AddEditFuelBinding {

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
            tv.setText(format.format(value))
        }
    }

    @JvmStatic
    @BindingAdapter("app:date")
    fun bindDate(tv: TextView, value: Calendar) {
        tv.setText(DateUtils.localizeDate(value, tv.context))
    }

    @JvmStatic
    @BindingAdapter("app:time")
    fun bindTime(tv: TextView, value: Calendar) {
        tv.setText(DateUtils.localizeTime(value, tv.context))
    }

}
