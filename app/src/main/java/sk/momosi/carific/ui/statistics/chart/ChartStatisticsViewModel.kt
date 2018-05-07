package sk.momosi.carific.ui.statistics.chart

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.*
import android.graphics.Color
import android.graphics.DashPathEffect
import android.support.v4.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chart_statistics.*
import sk.momosi.carific.Carific
import sk.momosi.carific.R
import sk.momosi.carific.Statistics
import sk.momosi.carific.model.*
import sk.momosi.carific.service.statistics.StatisticsService
import sk.momosi.carific.util.DateUtils
import sk.momosi.carific.util.firebase.db.toExpenseList
import sk.momosi.carific.util.firebase.db.toRefuelingList
import java.math.BigDecimal
import java.util.ArrayList


/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class ChartStatisticsViewModel : ViewModel() {

    val isLoading = ObservableBoolean(true)
    val isError = ObservableBoolean(false)
    val isEmpty = ObservableBoolean(false)

    var car = ObservableParcelable<Car>()
    var user = ObservableParcelable<User>()

    val data = MutableLiveData<ChartData>()

    private var refuelings: List<Refueling> = emptyList()

    fun load(car: Car, user: User) {
        this.car.set(car)
        this.user.set(user)

        val refuelingTask = fetchRefuelings(car)

        Tasks.whenAll(refuelingTask)
                .addOnSuccessListener {
                    refuelings = refuelingTask.result.filter { it.consumption != null }

                    data.value =
                            ChartData(
                                    prepareConsumptionChartData(),
                                    StatisticsService.getAverageConsumption(refuelings).toFloat(),
                                    prepareDates()
                            )

                    isEmpty.set(data.value?.consumptionChartData?.isEmpty() ?: false)
                    isLoading.set(false)

                }
                .addOnFailureListener {
                    isError.set(true)
                }
    }
    ;
    private fun prepareConsumptionChartData(): List<Entry> {

        val values = ArrayList<Entry>(refuelings.size)

        refuelings.forEachIndexed { index, refueling ->
            values.add(Entry(index.toFloat(), refueling.consumption?.toFloat() ?: 0f, null))
        }

        return values
    }

    private fun prepareDates(): List<String> {

        val values = ArrayList<String>(refuelings.size)

        refuelings.forEach { refueling ->
            values.add(DateUtils.localizeDate(refueling.date, Carific.context))
        }

        return values
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

    class ChartData(
            var consumptionChartData: List<Entry> = emptyList(),
            var averageConsumption: Float = 0f,
            var xAxis: List<String> = emptyList()
    )
}