package sk.momosi.carific.ui.car.edit

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific.R
import sk.momosi.carific.model.Car
import sk.momosi.carific.model.VehicleType
import sk.momosi.carific.util.data.SingleLiveEvent
import sk.momosi.carific.util.data.SnackbarMessage
import sk.momosi.carific.util.extensions.isNotNull
import sk.momosi.carific.util.extensions.isNotNullOrBlank
import java.io.File

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class AddEditCarViewModel(application: Application) : AndroidViewModel(application) {

    val name = ObservableField<String>()
    val isNameValid = ObservableBoolean(true)

    val manufacturer = ObservableField<String>()
    val isManufacturerValid = ObservableBoolean(true)

    val type = ObservableField<VehicleType>()

    var picturePath = ObservableField<String>()

    val isLoading = ObservableBoolean(false)

    val isCreateNew = ObservableBoolean(false)

    val taskFished = SingleLiveEvent<Car>()

    val selectPicture = SingleLiveEvent<String>()

    val snackbarMessage = SnackbarMessage()

    private var isLoaded: Boolean = false

    private var carId: String? = null

    init {
        name.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isNameValid.set(name.isNotNullOrBlank())
            }
        })

        manufacturer.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isManufacturerValid.set(manufacturer.isNotNullOrBlank())
            }
        })
    }

    fun start(carId: String?) {
        this.carId = carId

        if (isLoaded) {
            return
        }

        if (carId == null) {
            // No need to populate, it's a new car
            isCreateNew.set(true)
            return
        }

        isCreateNew.set(false)
        isLoading.set(true)

        //TODO load car and call onDataLoaded, with retrieved car
        FirebaseDatabase.getInstance()
                .getReference("user/${FirebaseAuth.getInstance().currentUser?.uid}/cars/$carId")
                .addListenerForSingleValueEvent(object : ValueEventListener {
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
        name.set(existingCar.name)
        manufacturer.set(existingCar.manufacturer)
        type.set(existingCar.type)

        if (existingCar.picturePath != null && File(existingCar.picturePath).exists()) {
            picturePath.set(existingCar.picturePath)
        }

        isLoading.set(false)
        isLoaded = true
    }

    // Called when clicking on add button.
    fun saveCar() {
        if (!isCarDataValid()) {
            snackbarMessage.value = R.string.car_create_validation_error
            return
        }

        var car = Car("", name.get()!!, manufacturer.get()!!, type.get()!!, picturePath.get())
        if (isCreateNew.get() || carId == null) {
            createCar(car)
        } else {
            updateCar(car)
        }
    }

    fun imageClicked() = selectPicture.call()

    private fun createCar(newCar: Car) {
        Log.d(TAG, "Creating car " + newCar)

        FirebaseDatabase.getInstance()
                .getReference("user/${FirebaseAuth.getInstance().currentUser?.uid}/cars")
                .push()
                .setValue(newCar.toMap())

        taskFished.value = newCar
    }

    private fun updateCar(car: Car) {
        Log.d(TAG, "Updating car " + car)

        if (isCreateNew.get()) {
            throw RuntimeException("updateCar() was called but car is new.")
        }

        FirebaseDatabase.getInstance()
                .getReference("user/${FirebaseAuth.getInstance().currentUser?.uid}/cars")
                .updateChildren(mapOf(Pair("$carId", car.toMap())))

        taskFished.value = car
    }

    fun removeCar() {
        Log.d(TAG, "Removing car " + carId)

        if (isCreateNew.get()) {
            throw RuntimeException("removeCar() was called but car is new.")
        }

        // Remove car
        FirebaseDatabase.getInstance()
                .getReference("user/${FirebaseAuth.getInstance().currentUser?.uid}/cars/$carId")
                .removeValue()

        // Remove refuelings
        FirebaseDatabase.getInstance()
                .getReference("fuel/$carId")
                .removeValue()

        // Remove expenses
        FirebaseDatabase.getInstance()
                .getReference("expense/$carId")
                .removeValue()

        taskFished.call()
    }

    private fun isCarDataValid(): Boolean {
        isNameValid.set(name.isNotNullOrBlank())
        isManufacturerValid.set(manufacturer.isNotNullOrBlank())

        return isNameValid.get() && isManufacturerValid.get()
    }

    companion object {
        val TAG = AddEditCarViewModel::class.java.simpleName
    }

}