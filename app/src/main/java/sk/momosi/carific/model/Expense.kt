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
data class Expense(
        var id: String = "",
        val price: BigDecimal,
        val date: Date,
        val info: String = ""
) : Parcelable, Comparable<Expense> {

    override fun compareTo(other: Expense) = date.compareTo(other.date)

    fun toMap(): Map<String, Any> = mapOf(
            Pair("price", price.toString()),
            Pair("date", date.time),
            Pair("note", info)
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>) = Expense(
                id = id,
                price = BigDecimal(map.get("price") as String),
                date = Date(map["date"] as Long),
                info = map["note"] as String
        )
    }

}


