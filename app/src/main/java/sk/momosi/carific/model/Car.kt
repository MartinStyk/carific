package sk.momosi.carific.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Car(
        val id: String = "",
        val name: String,
        val manufacturer: String,
        val type: VehicleType
//        val mileage: Long?,
//        val pathToPicure: String?
) : Parcelable


