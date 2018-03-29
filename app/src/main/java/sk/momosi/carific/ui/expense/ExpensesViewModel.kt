package sk.momosi.carific.ui.expense

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
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

    var expenses: MutableLiveData<List<Expense>> = MutableLiveData()

    fun getExpenses(): LiveData<List<Expense>> {
        FirebaseDatabase.getInstance()
                .getReference("test/expenses")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val list = mutableListOf<Expense>()
                            dataSnapshot.children.forEach {
                                list.add(Expense.fromMap(it.getValue() as Map<String, Any>))
                            }
                            expenses.postValue(list)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
        return expenses
    }
}