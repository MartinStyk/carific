package sk.momosi.carific.ui.statistics.detail

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
import sk.momosi.carific.util.firebase.db.TasksRepository
import sk.momosi.carific.util.firebase.db.toExpenseList
import sk.momosi.carific.util.firebase.db.toRefuelingList


/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class DetailedStatisticsViewModel : ViewModel() {

    val isLoading = ObservableBoolean(true)
    val isError = ObservableBoolean(false)

    val statistics = ObservableParcelable<Statistics>()

    var car = ObservableParcelable<Car>()
    var user = ObservableParcelable<User>()


    fun load(car: Car, user: User) {
        this.car.set(car)
        this.user.set(user)

        val refuelingTask = TasksRepository.fetchRefuelings(car.id)
        val expenseTask = TasksRepository.fetchExpenses(car.id)

        Tasks.whenAll(refuelingTask, expenseTask)
                .addOnSuccessListener {
                    statistics.set(StatisticsService(refuelingTask.getResult(),
                            expenseTask.getResult(),
                            VolumeUnit.LITRE).statistics)
                }
                .addOnFailureListener {
                    isError.set(true)
                }
    }

}