package sk.momosi.carific13

import android.app.Application
import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.squareup.leakcanary.LeakCanary

/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
class Carific: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        LeakCanary.install(this)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    companion object {

        private lateinit var instance: Carific

        val context: Context
            get() = instance.applicationContext
    }
}