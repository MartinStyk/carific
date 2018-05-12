package sk.momosi.carific.util.firebase.db

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific.model.Expense
import sk.momosi.carific.model.Refueling

/**
 * @return list of refuelings represented by this snapshot, sorted from the oldest to most recent
 */

object TasksRepository {

    fun fetchRefuelings(carId: String): Task<List<Refueling>> {
        val refuelingSource = TaskCompletionSource<List<Refueling>>()

        FirebaseDatabase.getInstance()
                .getReference("fuel/${carId}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) =
                            refuelingSource.setResult(dataSnapshot.toRefuelingList())

                    override fun onCancelled(databaseError: DatabaseError) =
                            refuelingSource.setException(databaseError.toException())
                })

        return refuelingSource.task
    }

    fun fetchExpenses(carId: String): Task<List<Expense>> {
        val expenseSource = TaskCompletionSource<List<Expense>>()

        FirebaseDatabase.getInstance()
                .getReference("expense/${carId}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) =
                            expenseSource.setResult(dataSnapshot.toExpenseList())

                    override fun onCancelled(databaseError: DatabaseError) =
                            expenseSource.setException(databaseError.toException())
                })

        return expenseSource.task
    }
}