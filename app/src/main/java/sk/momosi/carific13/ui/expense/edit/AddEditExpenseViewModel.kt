package sk.momosi.carific13.ui.expense.edit

import android.app.Application
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific13.model.Expense
import sk.momosi.carific13.ui.car.achievements.BaseAchievementViewModel
import sk.momosi.carific13.util.data.SingleLiveEvent
import sk.momosi.carific13.util.data.SnackbarMessage
import sk.momosi.carific13.util.extensions.isNotNull
import sk.momosi.carific13.util.extensions.isNotNullOrBlank
import java.math.BigDecimal
import java.util.*

/**
 * @author Lenka Heldová
 * @date 02.04.2018.
 */
class AddEditExpenseViewModel(application: Application) : BaseAchievementViewModel(application) {

    val price = ObservableField<BigDecimal>()
    val isPriceValid = ObservableBoolean(true)

    val date = ObservableField<Calendar>()

    val info = ObservableField<String>()
    val isInfoValid = ObservableBoolean(true)

    val isLoading = ObservableBoolean(false)

    val isCreateNew = ObservableBoolean(false)

    val taskFinished = SingleLiveEvent<Expense>()

    val taskError = SingleLiveEvent<Unit>()

    val snackbarMessage = SnackbarMessage()

    private var isLoaded: Boolean = false

    lateinit var carId: String

    lateinit var currency: String

    private var editedExpense: Expense? = null


    init {
        price.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isPriceValid.set(price.isNotNull())
            }
        })

        info.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isInfoValid.set(info.isNotNullOrBlank())
            }
        })
    }

    fun start(carId: String, expense: Expense? = null, currency: String) {
        this.carId = carId
        this.currency = currency

        if (isLoaded) {
            return
        }

        if (expense == null) {
            // No need to populate, it's a new expense
            isCreateNew.set(true)
            date.set(Calendar.getInstance()) // set current date
            return
        }

        editedExpense = expense

        isCreateNew.set(false)
        isLoading.set(true)

        populateFiels(expense)
    }

    private fun populateFiels(expense: Expense) {
        price.set(expense.price)

        val date = Calendar.getInstance()
        date.time = expense.date
        this.date.set(date)

        info.set(expense.info)

        isLoading.set(false)
        isLoaded = true
    }

    // Called when clicking on add button.
    fun saveExpense() {

        if (!isExpenseDataValid()) {
            taskError.call()
            return
        }

        val expense = Expense(
                id = editedExpense?.id ?: "",
                price = price.get()!!,
                info = info.get() ?: "",
                date = date.get()!!.time
        )

        if (isCreateNew.get() || editedExpense == null) {
            createExpense(expense)
            incrementExpenses()
        } else {
            updateExpense(expense)
        }
    }

    private fun isExpenseDataValid():Boolean {
        isPriceValid.set(price.isNotNull())
        isInfoValid.set(info.isNotNullOrBlank())

        return isPriceValid.get() && isInfoValid.get()
    }

    private fun createExpense(newExpense: Expense) {
        Log.d(TAG, "Creating expense " + newExpense)

        FirebaseDatabase.getInstance()
                .getReference("expense/$carId")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = mutableListOf<Expense>()

                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.forEach {
                                list.add(Expense.fromMap(it.key!!, it.value as Map<String, Any?>))
                            }
                        }
                        FirebaseDatabase.getInstance()
                                .getReference("expense/$carId")
                                .push()
                                .setValue(newExpense.toMap())
                    }

                    override fun onCancelled(p0: DatabaseError) = Unit
                })

        taskFinished.value = newExpense
    }

    private fun updateExpense(expense: Expense) {
        Log.d(TAG, "Updating expense " + expense)

        if (isCreateNew.get()) {
            throw RuntimeException("updateExpense() was called but expense is new.")
        }

        FirebaseDatabase.getInstance()
                .getReference("expense/$carId")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = mutableListOf<Expense>()

                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.forEach {
                                list.add(Expense.fromMap(it.key!!, it.value as Map<String, Any?>))
                            }
                        }
                        FirebaseDatabase.getInstance()
                                .getReference("expense/$carId/${expense.id}")
                                .removeValue()
                        FirebaseDatabase.getInstance()
                                .getReference("expense/$carId")
                                .push()
                                .setValue(expense.toMap())
                    }

                    override fun onCancelled(p0: DatabaseError) = Unit
                })

        taskFinished.value = expense
    }


    fun removeExpense() {
        Log.d(TAG, "Removing expense " + editedExpense)

        if (isCreateNew.get() || editedExpense == null) {
            throw RuntimeException("removeExpense() was called during create of new one.")
        }

        editedExpense?.let {
            FirebaseDatabase.getInstance()
                    .getReference("expense/$carId")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val list = mutableListOf<Expense>()

                            if (dataSnapshot.exists()) {
                                dataSnapshot.children.forEach {
                                    list.add(Expense.fromMap(it.key!!, it.value as Map<String, Any?>))
                                }
                            }
                            FirebaseDatabase.getInstance()
                                    .getReference("expense/$carId/${it.id}")
                                    .removeValue()
                        }

                        override fun onCancelled(p0: DatabaseError) = Unit
                    })
        }

        taskFinished.call()
    }

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

    companion object {
        val TAG = AddEditExpenseViewModel::class.java.simpleName
    }
}