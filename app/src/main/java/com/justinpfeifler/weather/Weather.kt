package com.justinpfeifler.weather

import java.sql.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

internal class Weather
(
        timeStamp: Long, minTemp: Double, maxTemp: Double,
        humidity: Double, val description: String, iconName: String
) {
    val dayOfWeek: String
    val minTemp: String
    val maxTemp: String
    val humidity: String
    val iconURL: String

    // class constructor
    init {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 0

        this.dayOfWeek = convertTimeStampToDay(timeStamp)
        this.minTemp = numberFormat.format(minTemp)
        this.maxTemp = numberFormat.format(maxTemp)
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0)
        this.iconURL = "http://openweathermap.org/img/w/$iconName.png"
    }

    // pattern EEEE returns full day name
    private fun convertTimeStampToDay(timeStamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp * 1000
        val zone = TimeZone.getDefault()

        calendar.add(
                Calendar.MILLISECOND,
                zone.getOffset(calendar.timeInMillis)
        )

        val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}