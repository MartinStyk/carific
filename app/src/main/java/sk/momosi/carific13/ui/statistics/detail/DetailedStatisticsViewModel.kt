package sk.momosi.carific13.ui.statistics.detail

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableParcelable
import com.google.android.gms.tasks.Tasks
import sk.momosi.carific13.Statistics
import sk.momosi.carific13.model.*
import sk.momosi.carific13.service.statistics.StatisticsService
import sk.momosi.carific13.util.firebase.db.TasksRepository


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
                    statistics.set(StatisticsService(refuelingTask.result ?: emptyList(),
                            expenseTask.result ?: emptyList(),
                            VolumeUnit.LITRE).statistics)
                }
                .addOnFailureListener {
                    isError.set(true)
                }
    }

}