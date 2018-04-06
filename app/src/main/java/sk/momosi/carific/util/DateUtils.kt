package sk.momosi.carific.util

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import java.util.*

object DateUtils {

    @JvmStatic
    fun localizeDate(date: Calendar, context: Context): String {
        Log.d(DateUtils::class.java.simpleName, "Formating $date")
        return DateFormat.getDateFormat(context).format(date.time)
    }


    @JvmStatic
    fun localizeTime(time: Calendar, context: Context):String {
        Log.d(DateUtils::class.java.simpleName, "Formating $time")
        return DateFormat.getTimeFormat(context).format(time.time)
    }
}