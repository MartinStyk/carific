package sk.momosi.carific13.util.firebase.db

import com.google.firebase.database.DataSnapshot
import sk.momosi.carific13.model.Expense
import sk.momosi.carific13.model.Refueling

/**
 * @return list of refuelings represented by this snapshot, sorted from the oldest to most recent
 */
fun DataSnapshot.toRefuelingList(): List<Refueling> {
    val list = mutableListOf<Refueling>()

    if (exists()) {
        children.forEach {
            list.add(Refueling.fromMap(it.key!!, it.value as Map<String, Any?>))
        }
    }

    return list.apply { sort() }
}


/**
 * @return list of expenses represented by this snapshot, sorted from the oldest to most recent
 */
fun DataSnapshot.toExpenseList(): List<Expense> {
    val list = mutableListOf<Expense>()

    if (exists()) {
        children.forEach {
            list.add(Expense.fromMap(it.key!!, it.value as Map<String, Any?>))
        }
    }

    return list.apply { sort() }
}