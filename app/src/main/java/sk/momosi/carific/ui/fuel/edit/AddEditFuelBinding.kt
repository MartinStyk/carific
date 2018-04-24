package sk.momosi.carific.ui.fuel.edit

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.support.design.widget.TextInputLayout
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
    @BindingAdapter("errorText")
    fun setErrorMessage(view: TextInputLayout, errorMessage: String) {
        view.error = errorMessage
    }

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
            val text = tv.text.toString().replace(",", ".")
            BigDecimal(text)
        }

        if (value != null && value != displayed) {
            tv.setText(DecimalFormat.getInstance().format(value))
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text")
    fun getBigDecimalFromBinding(view: TextView): BigDecimal? {
        return if (view.text.isNullOrBlank()) null else {
            val text = view.text.toString().replace(",", ".")
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
            BigDecimal(tv.text.replace(Regex("[^0-9]"), ""))
        }

        if (value != null && value != displayed) {
            tv.setText(format.format(value))
        }
    }

    @JvmStatic
    @BindingAdapter("date")
    fun bindDateFromCalendar(tv: TextView, value: Calendar) {
        tv.setText(DateUtils.localizeDate(value.time, tv.context))
    }

    @JvmStatic
    @BindingAdapter("date")
    fun bindDate(tv: TextView, value: Date) {
        tv.setText(DateUtils.localizeDate(value, tv.context))
    }


    @JvmStatic
    @BindingAdapter("time")
    fun bindTime(tv: TextView, value: Calendar) {
        tv.setText(DateUtils.localizeTime(value, tv.context))
    }

}
