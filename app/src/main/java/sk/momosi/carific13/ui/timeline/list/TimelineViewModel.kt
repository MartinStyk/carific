package sk.momosi.carific13.ui.timeline.list

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks
import sk.momosi.carific13.model.Expense
import sk.momosi.carific13.model.ListItem
import sk.momosi.carific13.model.Refueling
import sk.momosi.carific13.model.User
import sk.momosi.carific13.util.DateUtils
import sk.momosi.carific13.util.data.SingleLiveEvent
import sk.momosi.carific13.util.firebase.db.TasksRepository
import java.util.*

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class TimelineViewModel : ViewModel() {

    val items: ObservableList<ListItem> = ObservableArrayList()

    val isLoading = ObservableBoolean(true)

    val isEmpty = ObservableBoolean(false)

    val isError = ObservableBoolean(false)

    val refuelClickEvent = SingleLiveEvent<Refueling>()

    val expenseClickEvent = SingleLiveEvent<Expense>()

    lateinit var user: User

    lateinit var carId: String

    fun init(carId: String, user: User) {
        this.user = user

        this.carId = carId
    }

    fun loadData() {
        val refuelingTask = TasksRepository.fetchRefuelings(carId)
        val expenseTask = TasksRepository.fetchExpenses(carId)

        Tasks.whenAll(refuelingTask, expenseTask)
                .addOnSuccessListener {
                    if (expenseTask.exception != null && refuelingTask.exception != null) {
                        isError.set(true)
                        return@addOnSuccessListener
                    }

                    val list = mutableListOf<ListItem>()
                    list.addAll(refuelingTask.result ?: emptyList())
                    list.addAll(expenseTask.result ?: emptyList())

                    list.sort()

                    synchronized(this@TimelineViewModel) {
                        items.clear()
                        items.addAll(list)
                    }

                    isEmpty.set(list.isEmpty())
                    isLoading.set(false)
                }
                .addOnFailureListener {
                    isError.set(true)
                }
    }

    fun isSection(position: Int): Boolean {
        val date = Calendar.getInstance()
        val datePrevious = Calendar.getInstance()

        synchronized(this@TimelineViewModel) {
            date.time = items[position].date
            datePrevious.time = items[position - 1].date
        }

        return date.get(Calendar.MONTH) != datePrevious.get(Calendar.MONTH)
    }

    fun sectionName(position: Int): String {
        val date = Calendar.getInstance()

        synchronized(this) {
            if (position >= items.size || position < 0)
                return@synchronized
            date.time = items[position].date
        }

        return DateUtils.localizeMonthDate(date)
    }
}