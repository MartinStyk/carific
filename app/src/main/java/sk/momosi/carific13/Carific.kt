package sk.momosi.carific13

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import sk.momosi.carific13.dependencyinjection.app.DaggerApplicationComponent
import javax.inject.Inject

/**
 * @author Martin Styk
 * @date 30.03.2018.
 */

class Carific : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        DaggerApplicationComponent.builder().app(this).build().inject(this)

        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}