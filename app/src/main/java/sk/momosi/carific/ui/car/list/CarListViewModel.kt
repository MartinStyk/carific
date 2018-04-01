package sk.momosi.carific.ui.car.list

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific.model.Car
import sk.momosi.carific.util.data.SingleLiveEvent

/**
 * @author Martin Styk
 * @date 01.04.2018.
 */
class CarListViewModel : ViewModel() {

    val cars: MutableLiveData<List<Car>> = MutableLiveData()

    val isLoading = ObservableBoolean(true)

    val isEmpty = ObservableBoolean(false)

    val isError = ObservableBoolean(false)

    val carClickEvent = SingleLiveEvent<Car>()

    fun loadData() {
        FirebaseDatabase.getInstance()
                .getReference("user/${FirebaseAuth.getInstance().currentUser?.uid}/cars")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = mutableListOf<Car>()

                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.forEach {
                                list.add(Car.fromMap(it.key, it.value as Map<String, Any>))
                            }
                        }

                        cars.postValue(list)

                        isEmpty.set(list.isEmpty() || !dataSnapshot.exists())
                        isLoading.set(false)
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                        isError.set(true)
                    }
                })
    }

}