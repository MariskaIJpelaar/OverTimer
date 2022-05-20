package org.mariska.overtimer.utils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

//https://developer.android.com/codelabs/kotlin-android-training-room-database#3
@Entity(tableName="logged_times")
data class LogItem(
    @PrimaryKey(autoGenerate = false)
    var day: LocalDate = LocalDate.now(),
    @ColumnInfo(name="start_time")
    var startTime: LocalTime = LocalTime.now(),
    @ColumnInfo(name="end_time")
    var endTime: LocalTime = LocalTime.now(),
    @ColumnInfo(name="over_time")
    var overtime: Int = 0
)

data class TimeRange(
    @ColumnInfo(name="start_time")
    var startTime: LocalTime = LocalTime.now(),
    @ColumnInfo(name="end_time")
    var endTime: LocalTime = LocalTime.now()
)