package sk.momosi.carific.ui.fuel.edit

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.support.v7.widget.AppCompatEditText
import android.widget.TextView
import java.math.BigDecimal
import java.text.DecimalFormat


/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
object AddEditFuelBinding {

    @JvmStatic
    @BindingAdapter("android:text")
    fun bindIntegerInText(tv: AppCompatEditText, value: Int?) {

        val currentlyDisplayed = try {
            Integer.valueOf(tv.text.toString())
        } catch (e: NumberFormatException) {
            null
        }
        if (value != null && currentlyDisplayed != value)
            tv.setText(value.toString())
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text")
    fun getIntegerFromBinding(view: TextView): Int {
        return Integer.parseInt(view.text.toString())
    }

    @JvmStatic
    @BindingAdapter("android:text")
    fun bindBigDecimalInText(tv: AppCompatEditText, value: BigDecimal?) {
        val format = DecimalFormat.getInstance()

        val currentlyDisplayed = try {
            BigDecimal(tv.text.toString())
        } catch (e: Exception) {
            null
        }

        if (value != null && value != currentlyDisplayed) {
            tv.setText(format.format(value))
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text")
    fun getBigDecimalFromBinding(view: TextView): BigDecimal {
        return if (view.text.isNullOrBlank()) BigDecimal.ZERO else BigDecimal(view.text.toString())
    }

    @JvmStatic
    @BindingAdapter("app:price")
    fun bindPriceInText(tv: TextView, value: BigDecimal?) {
        if (value != null && tv.text.toString() != value.toString()) {
            val format = DecimalFormat.getCurrencyInstance()
            tv.setText(format.format(value))
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "app:price")
    fun getPriceFromBinding(view: TextView): BigDecimal {
        return if (view.text.isNullOrBlank()) BigDecimal.ZERO else BigDecimal(view.text.toString())
    }

}
