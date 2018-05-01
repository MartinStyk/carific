package sk.momosi.carific.ui.timeline.list

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific.model.*
import sk.momosi.carific.util.DateUtils
import sk.momosi.carific.util.data.SingleLiveEvent
import sk.momosi.carific.util.firebase.db.toExpenseList
import sk.momosi.carific.util.firebase.db.toRefuelingList
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
        val refuelingTask = fetchRefuelings(carId)
        val expenseTask = fetchExpenses(carId)

        Tasks.whenAll(refuelingTask, expenseTask)
                .addOnSuccessListener {
                    val list = mutableListOf<ListItem>()
                    list.addAll(refuelingTask.result)
                    list.addAll(expenseTask.result)

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

    private fun fetchRefuelings(carId: String): Task<List<Refueling>> {
        val refuelingSource = TaskCompletionSource<List<Refueling>>()

        FirebaseDatabase.getInstance()
                .getReference("fuel/${carId}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) =
                            refuelingSource.setResult(dataSnapshot.toRefuelingList())

                    override fun onCancelled(databaseError: DatabaseError) =
                            isError.set(true)
                })

        return refuelingSource.task
    }

    private fun fetchExpenses(carId: String): Task<List<Expense>> {
        val expenseSource = TaskCompletionSource<List<Expense>>()

        FirebaseDatabase.getInstance()
                .getReference("expense/${carId}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) =
                            expenseSource.setResult(dataSnapshot.toExpenseList())

                    override fun onCancelled(databaseError: DatabaseError) =
                            isError.set(true)
                })

        return expenseSource.task
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