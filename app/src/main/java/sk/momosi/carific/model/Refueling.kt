package sk.momosi.carific.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Refueling(
        var id: String = "",
        val distanceFromLast: Int,
        val volume: BigDecimal,
        val pricePerLitre: BigDecimal,
        val priceTotal: BigDecimal,
        val isFull: Boolean,
        val date: Date,
        val note: String = ""
) : Parcelable, Comparable<Refueling> {

    override fun compareTo(other: Refueling) = date.compareTo(other.date)

    fun toMap(): Map<String, Any> = mapOf(
            Pair("distanceFromLast", distanceFromLast),
            Pair("volume", volume.toString()),
            Pair("pricePerLitre", pricePerLitre.toString()),
            Pair("priceTotal", priceTotal.toString()),
            Pair("isFull", isFull),
            Pair("date", date.time),
            Pair("note", note)
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any>) = Refueling(
                id = id,
                distanceFromLast = (map["distanceFromLast"] as Long).toInt(),
                volume = BigDecimal(map.get("volume") as String),
                pricePerLitre = BigDecimal(map.get("pricePerLitre") as String),
                priceTotal = BigDecimal(map.get("priceTotal") as String),
                isFull = map.get("isFull") as Boolean,
                date = Date(map["date"] as Long),
                note = map["note"] as String
        )
    }

}


