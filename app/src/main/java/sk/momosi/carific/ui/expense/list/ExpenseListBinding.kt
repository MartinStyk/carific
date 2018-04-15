package sk.momosi.carific.ui.expense.list

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import sk.momosi.carific.R
import sk.momosi.carific.model.Expense
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
object ExpenseListBinding {

    @JvmStatic
    @BindingAdapter("expenses")
    fun setExpenses(recyclerView: RecyclerView, items: List<Expense>?) {
        val adapter = recyclerView.adapter
        if (adapter != null && adapter is ExpenseListAdapter) {
            adapter.replaceData(items ?: emptyList())
        }
    }

    @JvmStatic
    @BindingAdapter("value")
    fun setValueWithUnits(textView: TextView, value: String, unit: String) {
        textView.text = textView.context.getString(R.string.unit_text_placeholder, value, unit)
    }


    @JvmStatic
    @BindingAdapter("value", "unit")
    fun setValueWithUnits(textView: TextView, value: BigDecimal, unit: String) {
        textView.text = textView.context.getString(R.string.unit_text_placeholder,
                DecimalFormat.getInstance().format(value), unit)
    }
}