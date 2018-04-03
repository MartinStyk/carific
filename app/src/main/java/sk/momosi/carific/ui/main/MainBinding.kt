package sk.momosi.carific.ui.main

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
object MainBinding {

    @JvmStatic
    @BindingAdapter("app:vehicle_picture")
    fun setPicture(imageView: ImageView, pathToPicture: String?) {
        if (pathToPicture == null)
            return

        val file = File(pathToPicture)
        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .into(imageView)
        }
    }
}