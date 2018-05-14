package sk.momosi.carific13.util.extensions

import android.content.Context

fun Int.DP(context: Context): Float = (this * context.resources.displayMetrics.density)

fun Float.DP(context: Context): Float = (this * context.resources.displayMetrics.density)