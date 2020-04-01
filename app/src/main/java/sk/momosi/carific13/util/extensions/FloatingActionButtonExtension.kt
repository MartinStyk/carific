package sk.momosi.carific13.util.extensions

import android.view.animation.AnimationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import sk.momosi.carific13.R


fun FloatingActionButton.animateSuccess(duration: Long = 300) {
    val anim = AnimationUtils.loadAnimation(context, R.anim.rotate_180)
    anim.duration = duration
    startAnimation(anim);
}

fun FloatingActionButton.animateError(duration: Long = 300) {
    val anim = AnimationUtils.loadAnimation(context, R.anim.error_bounce)
    anim.duration = duration
    startAnimation(anim);
}


