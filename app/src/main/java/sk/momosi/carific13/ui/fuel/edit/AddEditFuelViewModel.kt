package sk.momosi.carific13.ui.fuel.edit

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific13.R
import sk.momosi.carific13.model.Refueling
import sk.momosi.carific13.model.User
import sk.momosi.carific13.service.fuel.FuelService
import sk.momosi.carific13.util.data.SingleLiveEvent
import sk.momosi.carific13.util.data.SnackbarMessage
import sk.momosi.carific13.util.extensions.isNotNull
import java.math.BigDecimal
import java.util.*

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class AddEditFuelViewModel(application: Application) : AndroidViewModel(application) {

    private val fuelService: FuelService by lazy { FuelService() }

    val distanceFromLast = ObservableField<Int>()
    val isDistanceFromLastValid = ObservableBoolean(true)

    val volume = ObservableField<BigDecimal>()
    val isVolumeValid = ObservableBoolean(true)

    val pricePerLitre = ObservableField<BigDecimal>()
    val isPricePerLitreValid = ObservableBoolean(true)

    val priceTotal = ObservableField<BigDecimal>()
    val isPriceTotalValid = ObservableBoolean(true)

    val isFull = ObservableField<Boolean>(true)

    val date = ObservableField<Calendar>()

    val note = ObservableField<String>()

    val isLoading = ObservableBoolean(false)

    val isCreateNew = ObservableBoolean(false)

    val taskFished = SingleLiveEvent<Refueling>()

    val taskError = SingleLiveEvent<Unit>()

    val snackbarMessage = SnackbarMessage()

    private var isLoaded: Boolean = false

    lateinit var carId: String

    lateinit var user: User

    private var editedRefueling: Refueling? = null


    init {
        distanceFromLast.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isDistanceFromLastValid.set(distanceFromLast.isNotNull())
            }
        })

        volume.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isVolumeValid.set(volume.isNotNull())
            }
        })

        pricePerLitre.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isPricePerLitreValid.set(pricePerLitre.isNotNull())
            }
        })

        priceTotal.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isPriceTotalValid.set(priceTotal.isNotNull())
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
            date.set(Calendar.getInstance()) // set current date
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

        val date = Calendar.getInstance()
        date.time = refueling.date
        this.date.set(date)

        note.set(refueling.note)

        isLoading.set(false)
        isLoaded = true
    }

    // Called when clicking on add button.
    fun saveRefueling() {

        if (!isRefuelingValid()) {
            taskError.call()
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
                date = date.get()!!.time
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
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = mutableListOf<Refueling>()

                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.forEach {
                                list.add(Refueling.fromMap(it.key!!, it.getValue() as Map<String, Any?>))
                            }
                        }
                        fuelService.insertRefueling(carId, newRefueling, list)
                    }

                    override fun onCancelled(p0: DatabaseError) = Unit
                })

        taskFished.value = newRefueling
    }

    private fun updateRefueling(refueling: Refueling) {
        Log.d(TAG, "Updating refueling " + refueling)

        if (isCreateNew.get()) {
            throw RuntimeException("updateRefueling() was called but refueling is new.")
        }

        FirebaseDatabase.getInstance()
                .getReference("fuel/$carId")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = mutableListOf<Refueling>()

                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.forEach {
                                list.add(Refueling.fromMap(it.key!!, it.getValue() as Map<String, Any?>))
                            }
                        }
                        fuelService.deleteFillUpInTransaction(carId, refueling, list)
                        fuelService.insertRefueling(carId, refueling, list)
                    }

                    override fun onCancelled(p0: DatabaseError) = Unit
                })

        taskFished.value = refueling
    }


    fun removeRefueling() {
        Log.d(TAG, "Removing refueling " + editedRefueling)

        if (isCreateNew.get() || editedRefueling == null) {
            throw RuntimeException("removeRefueling() was called during create of new one.")
        }

        editedRefueling?.let {
            FirebaseDatabase.getInstance()
                    .getReference("fuel/$carId")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val list = mutableListOf<Refueling>()

                            if (dataSnapshot.exists()) {
                                dataSnapshot.children.forEach {
                                    list.add(Refueling.fromMap(it.key!!, it.getValue() as Map<String, Any?>))
                                }
                            }
                            fuelService.deleteFillUpInTransaction(carId, it, list)
                        }

                        override fun onCancelled(p0: DatabaseError) = Unit
                    })
        }

        taskFished.call()
    }

    private fun isRefuelingValid(): Boolean {
        isDistanceFromLastValid.set(distanceFromLast.isNotNull())
        isVolumeValid.set(volume.isNotNull())
        isPriceTotalValid.set(priceTotal.isNotNull())
        isPricePerLitreValid.set(pricePerLitre.isNotNull())

        return isDistanceFromLastValid.get() && isVolumeValid.get() && isPriceTotalValid.get() && isPricePerLitreValid.get()
    }

    private fun isNotNull(observable: ObservableField<out Any>) = observable.get() != null

    fun setDate(year: Int, month: Int, day: Int) {
        date.get()?.set(Calendar.YEAR, year)
        date.get()?.set(Calendar.MONTH, month)
        date.get()?.set(Calendar.DAY_OF_MONTH, day)
        date.notifyChange()
    }

    fun setTime(hour: Int, minute: Int) {
        date.get()?.set(Calendar.HOUR, hour)
        date.get()?.set(Calendar.MINUTE, minute)
        date.notifyChange()
    }

    fun ocrCapturedFuel(priceTotal: BigDecimal, pricePerUnit: BigDecimal, volume: BigDecimal) {
        snackbarMessage.value = R.string.bill_capture_finished
        this.priceTotal.set(priceTotal)
        this.pricePerLitre.set(pricePerUnit)
        this.volume.set(volume)
    }

    companion object {
        val TAG = AddEditFuelViewModel::class.java.simpleName
    }

}