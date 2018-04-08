package sk.momosi.carific.ui.expense.list

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific.model.Expense
import sk.momosi.carific.model.User
import sk.momosi.carific.util.data.SingleLiveEvent

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class ExpensesViewModel : ViewModel() {

    val expenses: ObservableList<Expense> = ObservableArrayList()

    val isLoading = ObservableBoolean(true)

    val isEmpty = ObservableBoolean(false)

    val isError = ObservableBoolean(false)

    val expenseClickEvent = SingleLiveEvent<Expense>()

    lateinit var user: User

    lateinit var carId: String

    fun init(carId: String, user: User){
        this.user = user

        this.carId = carId
    }


    fun loadData(carId: String) {

        FirebaseDatabase.getInstance()
                .getReference("expense/$carId")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = mutableListOf<Expense>()

                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.forEach {
                                list.add(Expense.fromMap(it.key, it.value as Map<String, Any?>))
                            }
                        }

                        list.sort()
                        expenses.clear()
                        expenses.addAll(list)

                        isEmpty.set(list.isEmpty() || !dataSnapshot.exists())
                        isLoading.set(false)
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                        isError.set(true)
                    }
                })
    }
}