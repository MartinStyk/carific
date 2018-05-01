package sk.momosi.carific.model

import java.util.*

interface ListItem : Comparable<ListItem> {
    companion object {
        const val REFUELING = 1
        const val EXPENSE = 2
    }

    val listItemType: Int
    val date: Date

    override fun compareTo(other: ListItem) = date.compareTo(other.date)

}