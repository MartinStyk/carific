package sk.momosi.carific13.dependencyinjection.activity

import dagger.Module
import dagger.android.ContributesAndroidInjector
import sk.momosi.carific13.dependencyinjection.utils.ActivityScope
import sk.momosi.carific13.ui.car.edit.AddEditCarActivity
import sk.momosi.carific13.ui.car.list.CarChoiceListActivity
import sk.momosi.carific13.ui.car.list.CarListActivity
import sk.momosi.carific13.ui.expense.edit.AddEditExpenseActivity
import sk.momosi.carific13.ui.fuel.edit.AddEditFuelActivity
import sk.momosi.carific13.ui.login.LoginActivity
import sk.momosi.carific13.ui.main.MainActivity
import sk.momosi.carific13.ui.ocr.BillCaptureActivity
import sk.momosi.carific13.ui.statistics.detail.DetailedStatisticsActivity

@Module
interface ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityCommonModule::class])
    fun mainActivityInjector(): MainActivity?

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityCommonModule::class])
    fun addEditCarActivityInjector(): AddEditCarActivity?

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityCommonModule::class])
    fun carListActivityInjector(): CarListActivity?

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityCommonModule::class])
    fun carChoiceListActivityInjector(): CarChoiceListActivity?

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityCommonModule::class])
    fun addExpenseActivityInjector(): AddEditExpenseActivity?

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityCommonModule::class])
    fun addFuelActivityInjector(): AddEditFuelActivity?

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityCommonModule::class])
    fun loginActivityInjector(): LoginActivity?

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityCommonModule::class])
    fun billCaptureActivityInjector(): BillCaptureActivity?

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityCommonModule::class])
    fun detailedStatisticsActivityInjector(): DetailedStatisticsActivity?

}