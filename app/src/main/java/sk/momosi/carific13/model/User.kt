package sk.momosi.carific13.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Martin Styk
 * @date 31.03.2018.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class User(
        val id: String,
        val currencyCode: String,
        val currencySymbol: String,
        val defaultCar: String? = null
) : Parcelable {
    fun toMap(): Map<String, Any?> = mapOf(
            Pair("id", id),
            Pair("currencyCode", currencyCode),
            Pair("currencySymbol", currencySymbol),
            Pair("defaultCar", defaultCar)
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>) = User(
                id = id,
                currencyCode = map["currencyCode"] as String,
                currencySymbol = map["currencySymbol"] as String,
                defaultCar = if (map["defaultCar"] == null) null else map["defaultCar"] as String
        )
    }
}