package sk.momosi.carific.util.extensions

import android.support.design.widget.FloatingActionButton
import android.view.animation.AnimationUtils
import sk.momosi.carific.R


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


