package sk.momosi.carific.ui.fuel.edit

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import sk.momosi.carific.R
import sk.momosi.carific.model.Refueling
import sk.momosi.carific.model.User
import sk.momosi.carific.util.data.SingleLiveEvent
import sk.momosi.carific.util.data.SnackbarMessage
import java.math.BigDecimal
import java.util.*

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class AddEditFuelViewModel(application: Application) : AndroidViewModel(application) {

    val distanceFromLast = ObservableField<Int>()
    val isDistanceFromLastValid = ObservableBoolean(false)

    val volume = ObservableField<BigDecimal>()
    val isVolumeValid = ObservableBoolean(false)

    val pricePerLitre = ObservableField<BigDecimal>()
    val isPricePerLitreValid = ObservableBoolean(false)

    val priceTotal = ObservableField<BigDecimal>()
    val isPriceTotalValid = ObservableBoolean(false)

    val isFull = ObservableField<Boolean>()

    val date = ObservableField<Date>()

    val note = ObservableField<String>()

    val isLoading = ObservableBoolean(false)

    val isCreateNew = ObservableBoolean(false)

    val taskFished = SingleLiveEvent<Refueling>()

    val snackbarMessage = SnackbarMessage()

    private var isLoaded: Boolean = false

    lateinit var carId: String

    lateinit var user: User

    private var editedRefueling: Refueling? = null


    init {
        distanceFromLast.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isDistanceFromLastValid.set(distanceFromLast.get() != null)
            }
        })

        volume.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isVolumeValid.set(volume.get() != null)
            }
        })

        pricePerLitre.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isPricePerLitreValid.set(pricePerLitre.get() != null)
            }
        })

        priceTotal.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isPriceTotalValid.set(priceTotal.get() != null)
            }
        })
    }

    fun start(carId: String, refueling: Refueling? = null, user: User) {
        this.carId = carId
        this.user = user

        if (isLoaded) {
            return
        }

        if (refueling == null) {
            // No need to populate, it's a new refueling
            isCreateNew.set(true)
            date.set(Date()) // set current date
            return
        }

        editedRefueling = refueling

        isCreateNew.set(false)
        isLoading.set(true)

        populateFiels(refueling)
    }

    private fun populateFiels(refueling: Refueling) {
        distanceFromLast.set(refueling.distanceFromLast)
        volume.set(refueling.volume)
        pricePerLitre.set(refueling.pricePerLitre)
        priceTotal.set(refueling.priceTotal)
        isFull.set(refueling.isFull)
        date.set(refueling.date)
        note.set(refueling.note)

        isLoading.set(false)
        isLoaded = true
    }

    // Called when clicking on add button.
    fun saveRefueling() {

        if (!isDistanceFromLastValid.get() || !isVolumeValid.get() || !isPriceTotalValid.get() || !isPricePerLitreValid.get()) {
            snackbarMessage.value = R.string.refueling_create_validation_error
            return
        }

        val refueling = Refueling(
                id = editedRefueling?.id ?: "",
                distanceFromLast = distanceFromLast.get()!!,
                volume = volume.get()!!,
                priceTotal = priceTotal.get()!!,
                note = note.get() ?: "",
                pricePerLitre = pricePerLitre.get()!!,
                isFull = isFull.get() ?: false,
                date = date.get()!!
        )

        if (isCreateNew.get() || editedRefueling == null) {
            createRefueling(refueling)
        } else {
            updateRefueling(refueling)
        }
    }

    private fun createRefueling(newRefueling: Refueling) {
        Log.d(TAG, "Creating refueling " + newRefueling)

        FirebaseDatabase.getInstance()
                .getReference("fuel/$carId")
                .push()
                .setValue(newRefueling.toMap())

        taskFished.value = newRefueling
    }

    private fun updateRefueling(refueling: Refueling) {
        Log.d(TAG, "Updating refueling " + refueling)

        if (isCreateNew.get()) {
            throw RuntimeException("updateRefueling() was called but refueling is new.")
        }

        FirebaseDatabase.getInstance()
                .getReference("fuel/$carId")
                .updateChildren(mapOf(Pair("${refueling.id}", refueling.toMap())))

        taskFished.value = refueling
    }

    companion object {
        val TAG = AddEditFuelViewModel::class.java.simpleName
    }

    fun removeRefueling() {
        Log.d(TAG, "Removing refueling " + editedRefueling)

        if (isCreateNew.get()) {
            throw RuntimeException("removeRefueling() was called during create of new one.")
        }

        editedRefueling?.let {
            FirebaseDatabase.getInstance()
                    .getReference("fuel/$carId/${it.id}")
                    .removeValue()
        }

        taskFished.call()
    }
}