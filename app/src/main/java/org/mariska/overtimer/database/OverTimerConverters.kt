package org.mariska.overtimer.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

//https://developer.android.com/training/data-storage/room/referencing-data
class OverTimerConverters {
    @TypeConverter
    fun localDateToString(date: LocalDate?): String {
        return date.toString()
    }

    @TypeConverter
    fun stringToLocalDate(date: String?): LocalDate {
        return LocalDate.parse(date)
    }

    @TypeConverter
    fun localTimeToString(time: LocalTime?): String {
        return time.toString()
    }

    @TypeConverter
    fun stringToLocalTime(time: String?): LocalTime? {
        return LocalTime.parse(time)
    }
}