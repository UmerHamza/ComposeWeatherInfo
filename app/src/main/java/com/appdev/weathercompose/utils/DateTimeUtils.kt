package com.appdev.weathercompose.utils

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun getTimeStamp():String{
        val tsLong = System.currentTimeMillis() / 1000
        return tsLong.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimeStampToDate(timeStamp:String?): String {
        if(timeStamp.isNullOrEmpty()){
            return ""
        }
        val stamp = timeStamp.toLong()
        val objFormatter: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

        val objCalendar: Calendar = Calendar.getInstance()
        objCalendar.timeInMillis = stamp * 1000 //edit
        val result: String = objFormatter.format(objCalendar.getTime())
        objCalendar.clear()
        return result
    }

    @SuppressLint("SimpleDateFormat")
    fun changeDateFormat(inputFormat: String?, outputFormat: String?, inputDate: String?): String? {
        if (inputFormat == null || outputFormat == null || inputDate == null) {
            return null
        }
        var outputDate = ""
        try {
            val input = SimpleDateFormat(inputFormat)
            val output = SimpleDateFormat(outputFormat)
            input.parse(inputDate)?.let {
                outputDate = output.format(it)
            }
        } catch (e: Exception) {
            return null
        }
        return outputDate
    }

    fun convertMinutesToHoursMinutes(minutes:Int?): String {
        if(minutes == null){
            return ""
        }
        val hours: Int = minutes / 60
        val minutes: Int = minutes % 60
        return "${hours}h ${minutes}m"
    }
}