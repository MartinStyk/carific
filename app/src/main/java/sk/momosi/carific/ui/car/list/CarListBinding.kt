package sk.momosi.carific.ui.car.list

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import sk.momosi.carific.model.Car

/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
object CarListBinding {

    @JvmStatic
    @BindingAdapter("cars")
    fun setCars(recyclerView: RecyclerView, items: List<Car>?) {
        val adapter = recyclerView.adapter
        if (adapter != null && adapter is CarListAdapter) {
            adapter.replaceData(items ?: emptyList())
        }
    }
}