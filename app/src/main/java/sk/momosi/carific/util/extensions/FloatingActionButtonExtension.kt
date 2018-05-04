package sk.momosi.carific.util.extensions

import android.view.animation.AnimationUtils
import sk.momosi.carific.R
import sk.momosi.carific.view.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.hideAnimate() {
    if (!isShown) {
        return
    }
    cardView.clearAnimation()

    val xmlAnimationSample = AnimationUtils.loadAnimation(context,R.anim.jump_from_down);
//
    cardView.startAnimation(xmlAnimationSample);
}


