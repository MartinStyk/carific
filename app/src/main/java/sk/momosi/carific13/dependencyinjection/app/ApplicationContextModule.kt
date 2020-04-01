package sk.momosi.carific13.dependencyinjection.app

import android.app.Application
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import sk.momosi.carific13.dependencyinjection.utils.ForApplication
import javax.inject.Singleton


@Module
class ApplicationContextModule {

    @Provides
    @Singleton
    @ForApplication
    fun providesApplicationContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    @ForApplication
    fun providesResources(application: Application): Resources = application.resources

}