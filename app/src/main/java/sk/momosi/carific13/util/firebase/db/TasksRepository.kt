package sk.momosi.carific13.util.firebase.db

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific13.model.Expense
import sk.momosi.carific13.model.Refueling
import sk.momosi.carific13.model.User

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

    fun fetchUser(): Task<User> {
        val userSource = TaskCompletionSource<User>()

        FirebaseDatabase.getInstance()
                .getReference("user/${FirebaseAuth.getInstance().currentUser?.uid}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) =
                            userSource.setResult(
                                    User.fromMap(
                                            dataSnapshot.key!!,
                                            dataSnapshot.getValue() as Map<String, Any?>)
                            )

                    override fun onCancelled(databaseError: DatabaseError) =
                            userSource.setException(databaseError.toException())
                })

        return userSource.task
    }
}