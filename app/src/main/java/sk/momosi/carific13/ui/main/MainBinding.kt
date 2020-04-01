package sk.momosi.carific13.ui.main

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
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
        if (pathToPicture != null && File(pathToPicture).exists()) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Picasso.get()
                    .load(File(pathToPicture))
                    .into(imageView)
        } else {
            imageView.setImageDrawable(null)
        }
    }

    @JvmStatic
    @BindingAdapter("vehicle_picture", "vehicle_picture_empty")
    fun setPictureWithErrorPosibility(imageView: ImageView, pathToPicture: String?, @DrawableRes emptyPictureRes: Int) {
        if (pathToPicture != null && File(pathToPicture).exists()) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Picasso.get()
                    .load(File(pathToPicture))
                    .error(emptyPictureRes)
                    .into(imageView)
        } else {
            imageView.scaleType = ImageView.ScaleType.CENTER
            Picasso.get()
                    .load(emptyPictureRes)
                    .error(emptyPictureRes)
                    .into(imageView)
        }
    }

}