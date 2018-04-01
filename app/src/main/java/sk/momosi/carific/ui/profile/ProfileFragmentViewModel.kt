package sk.momosi.carific.ui.profile

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
class ProfileFragmentViewModel : ViewModel() {

    val car = MutableLiveData<Car>()

    val isCarLoading = ObservableBoolean(true)

    val userSignedOut = SingleLiveEvent<Unit>()

    fun start(carId: String) {
        FirebaseDatabase.getInstance()
                .getReference("user/${FirebaseAuth.getInstance().currentUser?.uid}/cars/$carId")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            onExistingCarLoaded(
                                    Car.fromMap(carId, dataSnapshot.getValue() as Map<String, Any>)
                            )
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) = Unit
                })
    }

    fun onExistingCarLoaded(existingCar: Car) {
        car.value = existingCar
        isCarLoading.set(false)
    }

}