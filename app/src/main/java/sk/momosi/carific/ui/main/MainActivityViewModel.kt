package sk.momosi.carific.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableBoolean
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific.model.Car
import sk.momosi.carific.model.User
import sk.momosi.carific.util.data.SingleLiveEvent

/**
 * @author Martin Styk
 * @date 31.03.2018.
 */
class MainActivityViewModel(app: Application) : AndroidViewModel(app) {

    val user: MutableLiveData<User> = MutableLiveData()

    lateinit var userLocal: User

    val isUserLoaded = ObservableBoolean(false)

    val car: MutableLiveData<Car?> = MutableLiveData()

    lateinit var carLocal: Car

    val isCarLoaded = ObservableBoolean(false)

    val requestLogin = SingleLiveEvent<Unit>()

    private val firebaseAuth = FirebaseAuth.getInstance()


    fun load() {
        if (!getLoggedUser()) {
            return
        }

        loadUser()

        loadCars()
    }

    private fun loadCars() {
        FirebaseDatabase.getInstance()
                .getReference("user/${firebaseAuth.currentUser?.uid}/cars")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val carData = dataSnapshot.children.first()
                            carLocal = Car.fromMap(carData.key, carData.getValue() as Map<String, Any>)
                            car.postValue(carLocal)
                            isCarLoaded.set(true)
                        } else {
                            car.postValue(null)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                    }
                })
    }

    private fun loadUser() {
        FirebaseDatabase.getInstance()
                .getReference("user/${firebaseAuth.currentUser?.uid}")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            userLocal = User.fromMap(dataSnapshot.key, dataSnapshot.getValue() as Map<String, Any>)
                            user.postValue(userLocal)
                            isUserLoaded.set(true)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                    }
                })
    }

    fun getLoggedUser() =
            if (firebaseAuth.currentUser == null) {
                requestLogin.call()
                false
            } else true

}
