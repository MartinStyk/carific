package sk.momosi.carific13.dependencyinjection.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import sk.momosi.carific13.dependencyinjection.utils.ViewModelFactory
import sk.momosi.carific13.dependencyinjection.utils.ViewModelKey
import sk.momosi.carific13.ui.car.edit.AddEditCarViewModel
import sk.momosi.carific13.ui.car.list.CarListViewModel
import sk.momosi.carific13.ui.expense.edit.AddEditExpenseViewModel
import sk.momosi.carific13.ui.fuel.edit.AddEditFuelViewModel
import sk.momosi.carific13.ui.main.MainActivityViewModel

@AssistedModule
@Module(includes = [AssistedInject_ViewModelsModule::class])
interface ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(value = MainActivityViewModel::class)
    fun mainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(value = AddEditCarViewModel::class)
    fun addEditCarActivityViewModel(viewModel: AddEditCarViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(value = CarListViewModel::class)
    fun carListActivityViewModel(viewModel: CarListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(value = AddEditExpenseViewModel::class)
    fun addEditExpenseViewModel(viewModel: AddEditExpenseViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(value = AddEditFuelViewModel::class)
    fun addEditFuelViewModel(viewModel: AddEditFuelViewModel): ViewModel

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}