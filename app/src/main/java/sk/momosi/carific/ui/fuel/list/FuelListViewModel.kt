package sk.momosi.carific.ui.fuel.list

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific.model.Refueling
import sk.momosi.carific.model.User
import sk.momosi.carific.util.DateUtils
import sk.momosi.carific.util.data.SingleLiveEvent
import java.util.*

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class FuelListViewModel : ViewModel() {

    val refuelings: ObservableList<Refueling> = ObservableArrayList()

    val isLoading = ObservableBoolean(true)

    val isEmpty = ObservableBoolean(false)

    val isError = ObservableBoolean(false)

    val refuelClickEvent = SingleLiveEvent<Refueling>()

    lateinit var user: User

    lateinit var carId: String

    fun init(carId: String, user: User) {
        this.user = user

        this.carId = carId
    }

    fun loadData(carId: String) {

        FirebaseDatabase.getInstance()
                .getReference("fuel/$carId")
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = mutableListOf<Refueling>()

                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.forEach {
                                list.add(Refueling.fromMap(it.key, it.getValue() as Map<String, Any?>))
                            }
                        }

                        list.sort()

                        synchronized(this@FuelListViewModel) {
                            refuelings.clear()
                            refuelings.addAll(list)
                        }

                        isEmpty.set(list.isEmpty() || !dataSnapshot.exists())
                        isLoading.set(false)
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                        isError.set(true)
                    }
                })
    }

    fun isSection(position: Int): Boolean {
        val date = Calendar.getInstance()
        val datePrevious = Calendar.getInstance()

        synchronized(this@FuelListViewModel) {
            date.time = refuelings[position].date
            datePrevious.time = refuelings[position - 1].date
        }

        return date.get(Calendar.MONTH) != datePrevious.get(Calendar.MONTH)
    }

    fun sectionName(position: Int): String {
        val date = Calendar.getInstance()

        synchronized(this) {
            date.time = refuelings[position].date
        }

        return DateUtils.localizeMonthDate(date)
    }
}