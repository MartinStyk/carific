package sk.momosi.carific.ui

import sk.momosi.carific.model.Expense

/**
 *
 * Listeners for interaction with recycler view items
 *
 * @author Martin Styk
 * @date 30.03.2018.
 */
interface ListItemUserInteractionListener<T> {
    fun onItemClick(item: T)
}