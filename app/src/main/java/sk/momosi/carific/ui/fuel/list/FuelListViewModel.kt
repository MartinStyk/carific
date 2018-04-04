package sk.momosi.carific.ui.fuel.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific.model.Refueling
import sk.momosi.carific.util.data.SingleLiveEvent

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class FuelListViewModel : ViewModel() {

    val refuelings: MutableLiveData<List<Refueling>> = MutableLiveData()

    val isLoading = ObservableBoolean(true)

    val isEmpty = ObservableBoolean(false)

    val isError = ObservableBoolean(false)

    val refuelClickEvent = SingleLiveEvent<Refueling>()

    fun loadData(carId: String): LiveData<List<Refueling>> {

        FirebaseDatabase.getInstance()
                .getReference("fuel/$carId")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = mutableListOf<Refueling>()

                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.forEach {
                                list.add(Refueling.fromMap(it.key, it.getValue() as Map<String, Any>))
                            }
                            refuelings.postValue(list)
                        }
                        isEmpty.set(list.isEmpty() || !dataSnapshot.exists())
                        isLoading.set(false)
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                        isError.set(true)
                    }
                })
        return refuelings
    }
}