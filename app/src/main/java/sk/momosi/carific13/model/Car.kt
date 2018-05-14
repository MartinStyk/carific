package sk.momosi.carific13.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import sk.momosi.carific13.R

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
        val type: VehicleType,
        val picturePath: String?,
        val volumeUnit: VolumeUnit= VolumeUnit.LITRE
//        val mileage: Long?,
) : Parcelable {

    fun toMap(): Map<String, Any> = mapOf(
            Pair("name", name),
            Pair("manufacturer", manufacturer),
            Pair("type", type.name),
            Pair("picturePath", picturePath ?: ""),
            Pair("volumeUnit", volumeUnit.name)

    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any>) = Car(
                id = id,
                name = map["name"] as String,
                manufacturer = map["manufacturer"] as String,
                type = VehicleType.valueOf(map["type"] as String),
                picturePath = map["picturePath"] as String,
                volumeUnit = VolumeUnit.valueOf(map["volumeUnit"] as String)
        )
    }
}


