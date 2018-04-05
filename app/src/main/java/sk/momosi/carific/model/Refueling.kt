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
        val note: String = "",
        val consumption: BigDecimal? = null
) : Parcelable, Comparable<Refueling> {

    override fun compareTo(other: Refueling) = date.compareTo(other.date).inv()

    fun toMap(): Map<String, Any?> = mapOf(
                Pair("distanceFromLast", distanceFromLast),
                Pair("volume", volume.toString()),
                Pair("pricePerLitre", pricePerLitre.toString()),
                Pair("priceTotal", priceTotal.toString()),
                Pair("isFull", isFull),
                Pair("date", date.time),
                Pair("note", if(note.isNullOrBlank()) null else note),
                Pair("consumption", consumption?.toString())
        )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>) = Refueling(
                id = id,
                distanceFromLast = (map["distanceFromLast"] as Long).toInt(),
                volume = BigDecimal(map["volume"] as String),
                pricePerLitre = BigDecimal(map["pricePerLitre"] as String),
                priceTotal = BigDecimal(map["priceTotal"] as String),
                isFull = map["isFull"] as Boolean,
                date = Date(map["date"] as Long),
                note = if (map["note"] == null) "" else map["note"] as String,
                consumption = if (map["consumption"] == null) null else BigDecimal(map["consumption"] as String)
        )
    }

}



