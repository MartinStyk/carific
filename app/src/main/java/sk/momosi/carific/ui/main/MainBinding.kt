package sk.momosi.carific.ui.main

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.squareup.picasso.Picasso
import java.io.File


/**
 * @author Martin Styk
 * @date 30.03.2018.
 */
object MainBinding {

    @JvmStatic
    @BindingAdapter("vehicle_picture")
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