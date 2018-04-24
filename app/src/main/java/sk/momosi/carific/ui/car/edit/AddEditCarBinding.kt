package sk.momosi.carific.ui.car.edit

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputLayout
import android.widget.ImageView
import com.squareup.picasso.Picasso
import sk.momosi.carific.R
import java.io.File


/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
object AddEditCarBinding {

    @JvmStatic
    @BindingAdapter("isValid", "errorText")
    fun setErrorMessage(view: TextInputLayout, isValid: Boolean, errorMessage: String) {
        if (isValid) {
            view.isErrorEnabled = false
        } else {
            view.isErrorEnabled = true
            view.error = errorMessage
        }
    }


    @JvmStatic
    @BindingAdapter("vehicle_picture_config")
    fun setPicture(imageView: ImageView, pathToPicture: String?) {
        if (pathToPicture != null && File(pathToPicture).exists()) {
            Picasso.get()
                    .load(File(pathToPicture))
                    .error(R.drawable.ic_add_a_photo)
                    .into(imageView)
        } else {
            Picasso.get()
                    .load(R.drawable.ic_add_a_photo)
                    .error(R.drawable.ic_add_a_photo)
                    .into(imageView)
        }
    }
}