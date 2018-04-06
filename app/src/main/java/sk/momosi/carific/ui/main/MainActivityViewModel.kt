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

    val carChange = SingleLiveEvent<Unit>()

    private val firebaseAuth = FirebaseAuth.getInstance()


    /**
     * Loads user and car
     * If user has default car specified, this car is loaded. Otherwise first user's car is used
     * If no car is available, create car activity is started
     */
    fun load() {
        if (!getLoggedUser()) {
            return
        }

        FirebaseDatabase.getInstance()
                .getReference("user/${firebaseAuth.currentUser?.uid}")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            userLocal = User.fromMap(dataSnapshot.key, dataSnapshot.getValue() as Map<String, Any?>)
                            user.postValue(userLocal)
                            isUserLoaded.set(true)


                            if (userLocal.defaultCar.isNullOrEmpty()) {
                                loadFirstCar(userLocal)
                            } else {
                                loadDefaultCar(userLocal)
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                    }
                })
    }


    private fun loadFirstCar(user: User) {

        FirebaseDatabase.getInstance()
                .getReference("user/${user.id}/cars")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val firstCarData = dataSnapshot.children.first()

                            val loadedCar = Car.fromMap(firstCarData.key, firstCarData.getValue() as Map<String, Any>)

                            if(::carLocal.isInitialized && loadedCar != carLocal){
                                carChange.call()
                            }

                            carLocal= loadedCar
                            car.postValue(carLocal)
                            isCarLoaded.set(true)
                        } else {
                            // car doesn't exist
                            car.postValue(null)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                    }
                })
    }

    private fun loadDefaultCar(user: User) {

        FirebaseDatabase.getInstance()
                .getReference("user/${user.id}/cars/${user.defaultCar}")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val loadedCar = Car.fromMap(dataSnapshot.key, dataSnapshot.getValue() as Map<String, Any>)

                            if(::carLocal.isInitialized && loadedCar != carLocal){
                                carChange.call()
                            }

                            carLocal= loadedCar
                            car.postValue(carLocal)
                            isCarLoaded.set(true)
                        } else {
                            loadFirstCar(user)
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
