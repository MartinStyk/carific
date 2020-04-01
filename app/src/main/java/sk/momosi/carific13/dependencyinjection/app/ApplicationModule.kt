package sk.momosi.carific13.dependencyinjection.app

import dagger.Module
import dagger.android.support.AndroidSupportInjectionModule
import sk.momosi.carific13.dependencyinjection.activity.ActivityBuilderModule

@Module(includes = [
    AndroidSupportInjectionModule::class,
    ApplicationContextModule::class,
    ActivityBuilderModule::class
])
internal abstract class ApplicationModule {

}