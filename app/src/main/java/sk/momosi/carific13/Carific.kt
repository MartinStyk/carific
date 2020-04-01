package sk.momosi.carific13

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
class Carific: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}