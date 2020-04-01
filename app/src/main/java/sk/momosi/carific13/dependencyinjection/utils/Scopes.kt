package sk.momosi.carific13.dependencyinjection.utils

import javax.inject.Qualifier
import javax.inject.Scope

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ForApplication

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ForActivity


@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentScope