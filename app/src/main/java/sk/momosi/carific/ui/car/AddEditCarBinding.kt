package sk.momosi.carific.ui.car

import android.databinding.BindingAdapter
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
}