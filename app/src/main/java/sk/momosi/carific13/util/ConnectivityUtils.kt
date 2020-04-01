package sk.momosi.carific13.util

import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.AnyThread

object ConnectivityUtils {

    @AnyThread
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        return activeNetworkInfo != null
    }
}