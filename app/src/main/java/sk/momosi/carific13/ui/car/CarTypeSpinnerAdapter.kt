package sk.momosi.carific13.ui.car

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.SpinnerAdapter
import android.widget.TextView
import sk.momosi.carific13.R
import sk.momosi.carific13.model.VehicleType
import java.lang.ref.WeakReference

/**
 * @author Martin Styk
 * @date 30.03.2018.
 */

class CarTypeSpinnerAdapter(context: Context) : BaseAdapter(), SpinnerAdapter {

    private val context = WeakReference(context)

    private val vehicleTypes = VehicleType.values()

    override fun getCount(): Int = vehicleTypes.size

    override fun getItem(position: Int) = vehicleTypes[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View? {

        return context.get()?.let {
            val spinView = view
                    ?: LayoutInflater.from(it).inflate(R.layout.item_vehicle_type, null)

            val picture = spinView.findViewById<ImageView>(R.id.vehicle_type_picture)
            val name = spinView.findViewById<TextView>(R.id.vehicle_type_name)

            name.setText(it.getString(vehicleTypes[position].type))
            picture.setImageResource(it.resources
                    .getIdentifier("ic_type_" + vehicleTypes[position].toString().toLowerCase(),
                            "drawable", context.get()?.packageName))
            spinView
        }
    }
}
