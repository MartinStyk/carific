package sk.momosi.carific13.ui.car.list

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import sk.momosi.carific13.model.Car
import sk.momosi.carific13.model.VehicleType

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

    @JvmStatic
    @BindingAdapter("car_type")
    fun setCarType(imageView: ImageView, carType: VehicleType) {
        imageView.setImageResource(carType.drawable)
    }
}