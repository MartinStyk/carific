package sk.momosi.carific13.dependencyinjection.activity

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import sk.momosi.carific13.dependencyinjection.utils.ActivityScope
import sk.momosi.carific13.dependencyinjection.utils.ForActivity

@Module
class ActivityContextModule {

    @Provides
    @ActivityScope
    @ForActivity
    fun activityContext(activity: Activity): Context = activity.baseContext

    @Provides
    @ActivityScope
    fun appCompatActivity(activity: Activity?): AppCompatActivity? = activity as AppCompatActivity?

}