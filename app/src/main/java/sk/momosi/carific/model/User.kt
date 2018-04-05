package sk.momosi.carific.model

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
        val currencySymbol: String
) : Parcelable {
    fun toMap(): Map<String, Any> = mapOf(
            Pair("id", id),
            Pair("currencyCode", currencyCode),
            Pair("currencySymbol", currencySymbol)
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any>) = User(
                id = id,
                currencyCode = map["currencyCode"] as String,
                currencySymbol = map["currencySymbol"] as String
        )
    }
}