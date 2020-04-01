package sk.momosi.carific13.ui.timeline.list

import androidx.fragment.app.Fragment
import sk.momosi.carific13.model.Car
import sk.momosi.carific13.model.User

open class CarificBaseFragment : Fragment() {

    val car: Car by lazy {
        arguments?.getParcelable<Car>(ARGUMENT_CAR)
                ?: throw IllegalArgumentException("Car argument missing")
    }

    val user by lazy {
        arguments?.getParcelable<User>(ARGUMENT_USER)
                ?: throw IllegalArgumentException("User argument missing")
    }

    companion object {
        const val ARGUMENT_CAR = "car"
        const val ARGUMENT_USER = "user"
    }
}
