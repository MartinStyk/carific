package sk.momosi.carific.ui.car

import android.databinding.BindingAdapter
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.support.annotation.StringRes
import android.widget.ImageView
import com.squareup.picasso.Picasso
import sk.momosi.carific.R
import java.io.File
import android.support.design.widget.TextInputLayout



/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
object AddEditCarBinding {

    @JvmStatic
    @BindingAdapter("app:vehicle_picture")
    fun setPicture(imageView: ImageView, pathToPicture: String?) {
        if (pathToPicture == null)
            return

        val file = File(pathToPicture)
        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .placeholder(R.drawable.ic_account_circle_100dp)
                    .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("app:isValid","app:errorText")
    fun setErrorMessage(view: TextInputLayout, isValid : ObservableBoolean, errorMessage: String) {
        isValid.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    view.error = if (isValid.get()) null else errorMessage
            }
        })
    }
}