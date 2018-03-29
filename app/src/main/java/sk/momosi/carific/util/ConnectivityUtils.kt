package sk.momosi.carific.util

import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.AnyThread

object ConnectivityUtils {

    @AnyThread
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        return activeNetworkInfo != null
    }
}