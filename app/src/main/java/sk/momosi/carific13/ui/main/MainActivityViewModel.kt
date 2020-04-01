package sk.momosi.carific13.ui.main

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableParcelable
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific13.dependencyinjection.utils.ForApplication
import sk.momosi.carific13.model.Car
import sk.momosi.carific13.model.User
import sk.momosi.carific13.util.data.SingleLiveEvent
import javax.inject.Inject

/**
 * @author Martin Styk
 * @date 31.03.2018.
 */
class MainActivityViewModel @Inject constructor(@ForApplication context: Context) : ViewModel() {

    val user: ObservableParcelable<User> = ObservableParcelable()

    val isUserLoaded = ObservableBoolean(false)

    val car: ObservableParcelable<Car> = ObservableParcelable()

    val isCarLoaded = ObservableBoolean(false)

    val requestLogin = SingleLiveEvent<Unit>()

    val carChange = SingleLiveEvent<Unit>()

    val noCarExists = SingleLiveEvent<Unit>()


    private val firebaseAuth = FirebaseAuth.getInstance()


    /**
     * Loads user and car
     * If user has default car specified, this car is loaded. Otherwise first user's car is used
     * If no car is available, create car activity is started
     */
    fun load() {
        if (!ensureUserLoggedIn()) {
            return
        }

        FirebaseDatabase.getInstance()
                .getReference("user/${firebaseAuth.currentUser?.uid}")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            user.set(User.fromMap(dataSnapshot.key!!, dataSnapshot.getValue() as Map<String, Any?>))
                            isUserLoaded.set(true)

                            if (user.get()?.defaultCar.isNullOrEmpty()) {
                                loadFirstCar()
                            } else {
                                loadDefaultCar()
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
    }


    private fun loadFirstCar() {

        user.get()?.let {
            FirebaseDatabase.getInstance()
                    .getReference("user/${it.id}/cars")
                    .addValueEventListener(object : ValueEventListener {

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val firstCarData = dataSnapshot.children.first()

                                val nextDisplayedCar = Car.fromMap(firstCarData.key!!, firstCarData.value as Map<String, Any>)
                                val currentlyDisplayedCar = car.get()

                                car.set(nextDisplayedCar)

                                if (nextDisplayedCar != currentlyDisplayedCar) {
                                    carChange.call()
                                }

                                isCarLoaded.set(true)
                            } else {
                                noCarExists.call()
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
        }
    }

    private fun loadDefaultCar() {
        user.get()?.let {

            FirebaseDatabase.getInstance()
                    .getReference("user/${it.id}/cars/${it.defaultCar}")
                    .addValueEventListener(object : ValueEventListener {

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val nextDisplayedCar = Car.fromMap(dataSnapshot.key!!, dataSnapshot.getValue() as Map<String, Any>)
                                val currentlyDisplayedCar = car.get()

                                car.set(nextDisplayedCar)

                                if (nextDisplayedCar != currentlyDisplayedCar) {
                                    carChange.call()
                                }

                                isCarLoaded.set(true)
                            } else {
                                loadFirstCar()
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
        }
    }


    fun ensureUserLoggedIn() =
            if (firebaseAuth.currentUser == null) {
                requestLogin.call()
                false
            } else true

}
