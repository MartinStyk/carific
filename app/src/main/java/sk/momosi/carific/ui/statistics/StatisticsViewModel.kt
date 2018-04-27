package sk.momosi.carific.ui.statistics

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableParcelable
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific.Statistics
import sk.momosi.carific.model.*
import sk.momosi.carific.service.statistics.StatisticsService
import sk.momosi.carific.util.firebase.db.toExpenseList
import sk.momosi.carific.util.firebase.db.toRefuelingList


/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class StatisticsViewModel : ViewModel() {

    val isLoading = ObservableBoolean(true)
    val isError = ObservableBoolean(false)

    val statistics = ObservableParcelable<Statistics>()

    var car = ObservableParcelable<Car>()
    var user = ObservableParcelable<User>()


    fun load(car: Car, user: User) {
        this.car.set(car)
        this.user.set(user)

        val refuelingTask = fetchRefuelings(car)
        val expenseTask = fetchExpenses(car)

        Tasks.whenAll(refuelingTask, expenseTask)
                .addOnSuccessListener {

                    //                    Handler().post {
                    statistics.set(StatisticsService(refuelingTask.getResult(),
                            expenseTask.getResult(),
                            VolumeUnit.LITRE).statistics)
//                    }

                }
                .addOnFailureListener {
                    isError.set(true)
                }
    }

    private fun fetchRefuelings(car: Car): Task<List<Refueling>> {
        val refuelingSource = TaskCompletionSource<List<Refueling>>()

        FirebaseDatabase.getInstance()
                .getReference("fuel/${car.id}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) =
                            refuelingSource.setResult(dataSnapshot.toRefuelingList())

                    override fun onCancelled(databaseError: DatabaseError) =
                            isError.set(true)
                })

        return refuelingSource.task
    }

    private fun fetchExpenses(car: Car): Task<List<Expense>> {
        val expenseSource = TaskCompletionSource<List<Expense>>()

        FirebaseDatabase.getInstance()
                .getReference("expense/${car.id}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) =
                            expenseSource.setResult(dataSnapshot.toExpenseList())

                    override fun onCancelled(databaseError: DatabaseError) =
                            isError.set(true)
                })

        return expenseSource.task
    }

}