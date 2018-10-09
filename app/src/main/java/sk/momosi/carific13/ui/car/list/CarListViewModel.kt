package sk.momosi.carific13.ui.car.list

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import android.graphics.drawable.Icon
import android.os.Build
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.momosi.carific13.R
import sk.momosi.carific13.model.Car
import sk.momosi.carific13.ui.fuel.edit.AddEditFuelActivity
import sk.momosi.carific13.ui.main.MainActivity
import sk.momosi.carific13.util.data.SingleLiveEvent
import sk.momosi.carific13.util.firebase.db.TasksRepository
import java.util.Arrays.asList

/**
 * @author Martin Styk
 * @date 01.04.2018.
 */
class CarListViewModel : ViewModel() {

    val cars: ObservableList<Car> = ObservableArrayList()

    val isLoading = ObservableBoolean(true)

    val isEmpty = ObservableBoolean(false)

    val isError = ObservableBoolean(false)

    val carClickEvent = SingleLiveEvent<Car>()

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun loadData() {
        FirebaseDatabase.getInstance()
                .getReference("user/${FirebaseAuth.getInstance().currentUser?.uid}/cars")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        cars.clear()

                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.forEach {
                                cars.add(Car.fromMap(it.key!!, it.value as Map<String, Any>))
                            }
                        }

                        isEmpty.set(cars.isEmpty() || !dataSnapshot.exists())
                        isLoading.set(false)
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        isError.set(true)
                    }
                })
    }

    fun setDefaultCar(car: Car, context: Context) {
        FirebaseDatabase.getInstance()
                .getReference("user/${FirebaseAuth.getInstance().currentUser?.uid}/defaultCar")
                .setValue(car.id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val fetchUser = TasksRepository.fetchUser()
            Tasks.whenAll(fetchUser).addOnSuccessListener {
                // TODO activities back stack does not work
                val int1 = Intent(Intent.ACTION_MAIN)
                val int2 = Intent(Intent.ACTION_VIEW, null, context, AddEditFuelActivity::class.java)
                int2.putExtra(AddEditFuelActivity.ARG_CAR_ID, car.id)
                int2.putExtra(AddEditFuelActivity.ARG_CURRENCY, fetchUser.result?.currencySymbol)

                // TODO translations
                val shortcut = ShortcutInfo.Builder(context, "add_fill_up_shortcut")
                        .setShortLabel("Pridaj ${car.name}")
                        .setLongLabel("Pridaj tankovanie pre ${car.name}")
                        .setIcon(Icon.createWithResource(context, R.drawable.ic_gas_station))
                        .setIntents(arrayOf(int1, int2))
                        .build()

                val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
                shortcutManager.dynamicShortcuts = asList(shortcut)

            }
        }
    }

}