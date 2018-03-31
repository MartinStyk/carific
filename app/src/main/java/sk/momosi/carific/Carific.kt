package sk.momosi.carific

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.squareup.leakcanary.LeakCanary

/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
class Carific: Application() {

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        LeakCanary.install(this)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}