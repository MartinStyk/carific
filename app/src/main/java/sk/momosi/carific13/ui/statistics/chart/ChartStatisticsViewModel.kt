package sk.momosi.carific13.ui.statistics.chart

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.*

import android.util.Log
import android.widget.DatePicker
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.github.mikephil.charting.data.Entry
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific13.Carific
import sk.momosi.carific13.model.*
import sk.momosi.carific13.service.statistics.StatisticsService
import sk.momosi.carific13.util.DateUtils
import sk.momosi.carific13.util.data.SingleLiveEvent
import sk.momosi.carific13.util.firebase.db.TasksRepository
import sk.momosi.carific13.util.firebase.db.toRefuelingList
import java.util.*


/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class ChartStatisticsViewModel : ViewModel(), DatePickerDialog.OnDateSetListener {

    val isEmpty = ObservableBoolean(false)

    var car = ObservableParcelable<Car>()
    var user = ObservableParcelable<User>()

    val data = MutableLiveData<ChartData>()

    var selectedRangeStart = ObservableField<Calendar>(Calendar.getInstance().apply { roll(Calendar.MONTH, -2) })
    var selectedRangeEnd = ObservableField<Calendar>(Calendar.getInstance())

    private var allRefuelings: List<Refueling> = emptyList()
    private var timeFilteredRefuelings: List<Refueling> = emptyList()

    fun init(car: Car, user: User) {
        this.car.set(car)
        this.user.set(user)
    }

    fun load() {

        val refuelingTask = TasksRepository.fetchRefuelings(car.get()!!.id)

        Tasks.whenAll(refuelingTask)
                .addOnSuccessListener { it ->
                    allRefuelings = refuelingTask.result
                            ?.filter { it.consumption != null } ?: emptyList()

                    timeFilteredRefuelings = allRefuelings
                            .filter { it.date.time in selectedRangeStart.get()!!.time.time..selectedRangeEnd.get()!!.time.time }

                    data.value =
                            ChartData(
                                    consumptionChartData = prepareConsumptionChartData(),
                                    averageAllTimeConsumption = StatisticsService.getAverageConsumption(allRefuelings).toFloat(),
                                    xAxis = prepareDates()
                            )

                    isEmpty.set(data.value?.consumptionChartData?.isEmpty() ?: false)
                }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        selectedRangeStart.set(Calendar.getInstance().apply {
            set(year, monthOfYear, dayOfMonth, 0, 0)
        })

        selectedRangeEnd.set(Calendar.getInstance().apply {
            set(yearEnd, monthOfYearEnd, dayOfMonthEnd, 23, 59)
        })

        load()
    }

    fun consumptionMin(): Float =
            timeFilteredRefuelings.minBy {
                it.consumption?.toFloat() ?: Float.MAX_VALUE
            }?.consumption?.toFloat() ?: 0f

    fun consumptionMax(): Float =
            timeFilteredRefuelings.maxBy {
                it.consumption?.toFloat() ?: Float.MIN_VALUE
            }?.consumption?.toFloat() ?: 0f

    private fun prepareConsumptionChartData(): List<Entry> {
        return timeFilteredRefuelings
                .mapIndexedTo(ArrayList(timeFilteredRefuelings.size), { index, refueling ->
                    Entry(index.toFloat(), refueling.consumption?.toFloat() ?: 0f, null)
                })
    }

    private fun prepareDates(): List<String> {
        return timeFilteredRefuelings.mapTo(ArrayList(timeFilteredRefuelings.size)) { refueling ->
            DateUtils.localizeDate(refueling.date, Carific.context)
        }

    }

    class ChartData(
            var consumptionChartData: List<Entry> = emptyList(),
            var averageAllTimeConsumption: Float = 0f,
            var xAxis: List<String> = emptyList()
    )
}