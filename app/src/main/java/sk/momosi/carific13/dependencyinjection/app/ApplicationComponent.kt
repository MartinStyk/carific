package sk.momosi.carific13.dependencyinjection.app

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import sk.momosi.carific13.Carific
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, AndroidSupportInjectionModule::class])
interface ApplicationComponent : AndroidInjector<Carific> {

    fun inject(application: Application)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun app(application: Application): Builder

        fun build(): ApplicationComponent
    }
}