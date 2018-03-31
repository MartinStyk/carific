package sk.momosi.carific.ui.car

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import sk.momosi.carific.R
import sk.momosi.carific.model.Car
import sk.momosi.carific.model.VehicleType
import sk.momosi.carific.util.data.SingleLiveEvent
import sk.momosi.carific.util.data.SnackbarMessage
import java.io.File

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class AddEditCarViewModel(application: Application) : AndroidViewModel(application) {

    val name = ObservableField<String>()
    val isNameValid = ObservableBoolean(false)

    val manufacturer = ObservableField<String>()
    val isManufacturerValid = ObservableBoolean(false)

    val type = ObservableField<VehicleType>()

    var picturePath = ObservableField<String>()

    val isLoading = ObservableBoolean(false)

    val taskFished = SingleLiveEvent<Car>()

    val selectPicture = SingleLiveEvent<String>()

    val snackbarMessage = SnackbarMessage()

    private var isNewCar: Boolean = false

    private var isLoaded: Boolean = false

    private var carId: String? = null

    init {
        name.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isNameValid.set(!name.get().isNullOrBlank())
            }
        })

        manufacturer.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isManufacturerValid.set(!manufacturer.get().isNullOrBlank())
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
            isNewCar = true
            return
        }

        isNewCar = false
        isLoading.set(true)

        //TODO load car and call onDataLoaded, with retrieved car
        //temporary debug with sample car
        onExistingCarLoaded(Car("id", "name", "manufacturer", VehicleType.MOTOCYCLE, "/data/dsaduser/0/sk.momosi.carific/files/1522439720639.jpg"))
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
        if (!isManufacturerValid.get() || !isNameValid.get()) {
            snackbarMessage.value = R.string.car_create_validation_error
            return
        }

        var car = Car("", name.get()!!, manufacturer.get()!!, type.get()!!, picturePath.get())
        if (isNewCar || carId == null) {
            createCar(car)
        } else {
            updateCar(car)
        }
    }

    fun imageClicked() = selectPicture.call()

    private fun createCar(newCar: Car) {
        Log.d(TAG, "Creating car " + newCar)
        // TODO save in DB

        taskFished.value = newCar
    }

    private fun updateCar(car: Car) {
        Log.d(TAG, "Updating car " + car)

        if (isNewCar) {
            throw RuntimeException("updateCar() was called but car is new.")
        }
        // TODO update in DB

        taskFished.value = car
    }

    companion object {
        val TAG = AddEditCarViewModel::class.java.simpleName
    }
}