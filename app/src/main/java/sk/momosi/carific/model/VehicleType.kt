package sk.momosi.carific.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import sk.momosi.carific.R

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
enum class VehicleType(@StringRes val type: Int, @DrawableRes val drawable: Int) {
    SEDAN(R.string.vehicle_type_sedan, R.drawable.ic_type_sedan),
    HATCHBACK(R.string.vehicle_type_hatchback, R.drawable.ic_type_hatchback),
    COMBI(R.string.vehicle_type_combi, R.drawable.ic_type_combi),
    VAN(R.string.vehicle_type_van, R.drawable.ic_type_van),
    MOTOCYCLE(R.string.vehicle_type_motocycle, R.drawable.ic_type_motocycle),
    PICKUP(R.string.vehicle_type_pickup, R.drawable.ic_type_pickup),
    SUV(R.string.vehicle_type_suv, R.drawable.ic_type_suv),
    COUPE(R.string.vehicle_type_coupe, R.drawable.ic_type_coupe)
}


