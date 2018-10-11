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
import android.support.annotation.RequiresApi
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
            setShortcuts(context, car)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun setShortcuts(context: Context, car: Car) {
        val fetchUser = TasksRepository.fetchUser()
        Tasks.whenAll(fetchUser).addOnSuccessListener {
            val intentMain = Intent(Intent.ACTION_MAIN, null, context, MainActivity::class.java)
            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            val intentAddFuel = Intent(Intent.ACTION_VIEW, null, context, AddEditFuelActivity::class.java)
            intentAddFuel.putExtra(AddEditFuelActivity.ARG_CAR_ID, car.id)
            intentAddFuel.putExtra(AddEditFuelActivity.ARG_CURRENCY, fetchUser.result?.currencySymbol)

            val intentAddFuelCamera = Intent(Intent.ACTION_VIEW, null, context, AddEditFuelActivity::class.java)
            intentAddFuelCamera.putExtra(AddEditFuelActivity.ARG_CAR_ID, car.id)
            intentAddFuelCamera.putExtra(AddEditFuelActivity.ARG_CURRENCY, fetchUser.result?.currencySymbol)
            intentAddFuelCamera.putExtra(AddEditFuelActivity.ARG_OPEN_OCR, true)

            val addFillup = ShortcutInfo.Builder(context, "shortcut_add_fillup")
                    .setShortLabel(context.getString(R.string.shortcut_add_fuel_short))
                    .setLongLabel(context.getString(R.string.shortcut_add_fuel_long, car.name))
                    .setIcon(Icon.createWithResource(context, R.drawable.ic_gas_station))
                    .setIntents(arrayOf(intentMain, intentAddFuel))
                    .build()

            val scanBill = ShortcutInfo.Builder(context, "shortcut_scan_fillup")
                    .setShortLabel(context.getString(R.string.shortcut_scan_bill_short))
                    .setLongLabel(context.getString(R.string.shortcut_scan_bill_long, car.name))
                    .setIcon(Icon.createWithResource(context, R.drawable.ic_camera))
                    .setIntents(arrayOf(intentMain, intentAddFuelCamera))
                    .build()

            val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
            shortcutManager.dynamicShortcuts = asList(addFillup, scanBill)
        }
    }
}
