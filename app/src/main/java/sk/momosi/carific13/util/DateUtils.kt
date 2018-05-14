package sk.momosi.carific13.util

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    @JvmStatic
    fun localizeDate(date: Date, context: Context): String {
        Log.d(DateUtils::class.java.simpleName, "Formating $date")
        return DateFormat.getDateFormat(context).format(date.time)
    }


    @JvmStatic
    fun localizeTime(time: Calendar, context: Context):String {
        Log.d(DateUtils::class.java.simpleName, "Formating $time")
        return DateFormat.getTimeFormat(context).format(time.time)
    }

    @JvmStatic
    fun localizeMonthDate(date: Calendar): String {
        Log.d(DateUtils::class.java.simpleName, "Formating $date")
        val monthNameAndYearFormat = SimpleDateFormat("MMMM yyyy")
        return monthNameAndYearFormat.format(date.time)
    }


}