package sk.momosi.carific.ui.expense

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import sk.momosi.carific.model.Expense

/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
object ExpenseListBinding {

    @JvmStatic
    @BindingAdapter("app:expenses")
    fun setExpenses(recyclerView: RecyclerView, items: List<Expense>?) {
        val adapter = recyclerView.adapter
        if (adapter != null && adapter is ExpenseListAdapter) {
            adapter.replaceData(items ?: emptyList())
        }
    }
}