package sk.momosi.carific.ui.fuel.list

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import sk.momosi.carific.model.Refueling

/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
object FuelListBinding {

    @JvmStatic
    @BindingAdapter("refuelings")
    fun setExpenses(recyclerView: RecyclerView, items: List<Refueling>?) {
        val adapter = recyclerView.adapter
        if (adapter != null && adapter is FuelListAdapter) {
            adapter.replaceData(items ?: emptyList())
        }
    }
}