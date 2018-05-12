package sk.momosi.carific.ui.timeline.list

import android.support.v4.app.Fragment
import sk.momosi.carific.model.Car
import sk.momosi.carific.model.User

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
