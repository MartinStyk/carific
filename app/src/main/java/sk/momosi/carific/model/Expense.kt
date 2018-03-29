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
        val price: BigDecimal,
        val date: Date,
        val info: String = ""
) : Parcelable, Comparable<Expense> {

    override fun compareTo(other: Expense) = date.compareTo(other.date)
}


