package sk.momosi.carific.ui.expense

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific.model.Expense

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class ExpensesViewModel : ViewModel() {

    val expenses: MutableLiveData<List<Expense>> = MutableLiveData()

    val isLoading = ObservableBoolean(true)

    val isEmpty = ObservableBoolean(false)

    val isError = ObservableBoolean(false)

    fun loadData(): LiveData<List<Expense>> {
        FirebaseDatabase.getInstance()
                .getReference("test/expenses")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = mutableListOf<Expense>()

                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.forEach {
                                list.add(Expense.fromMap(it.getValue() as Map<String, Any>))
                            }
                            expenses.postValue(list)
                        }
                        isEmpty.set(list.isEmpty() || !dataSnapshot.exists())
                        isLoading.set(false)
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                        isError.set(true)
                    }
                })
        return expenses
    }
}