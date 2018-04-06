package sk.momosi.carific.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableParcelable
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
                            user.set(User.fromMap(dataSnapshot.key, dataSnapshot.getValue() as Map<String, Any?>))
                            isUserLoaded.set(true)


                            if (user.get()?.defaultCar.isNullOrEmpty()) {
                                loadFirstCar()
                            } else {
                                loadDefaultCar()
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {
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

                                val loadedCar = Car.fromMap(firstCarData.key, firstCarData.getValue() as Map<String, Any>)

                                if (car.get() != null && loadedCar != car.get()) {
                                    carChange.call()
                                }

                                car.set(loadedCar)
                                isCarLoaded.set(true)
                            } else {
                                noCarExists.call()
                            }
                        }

                        override fun onCancelled(p0: DatabaseError?) {
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
                                val loadedCar = Car.fromMap(dataSnapshot.key, dataSnapshot.getValue() as Map<String, Any>)

                                if (car.get() != null && loadedCar != car.get()) {
                                    carChange.call()
                                }

                                car.set(loadedCar)
                                isCarLoaded.set(true)
                            } else {
                                loadFirstCar()
                            }
                        }

                        override fun onCancelled(p0: DatabaseError?) {
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
