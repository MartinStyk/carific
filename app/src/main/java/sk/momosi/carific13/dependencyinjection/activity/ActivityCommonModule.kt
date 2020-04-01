package sk.momosi.carific13.dependencyinjection.activity

import dagger.Module

@Module(includes = [
    ActivityContextModule::class,
    ViewModelsModule::class
])
abstract class ActivityCommonModule