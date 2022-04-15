package org.mariska.overtimer.utils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

//https://developer.android.com/codelabs/kotlin-android-training-room-database#3
@Entity(tableName="logged_times")
data class LogItem(
    @PrimaryKey(autoGenerate = true)
    var logId: Long = 0L,
    @ColumnInfo
    var day: LocalDate = LocalDate.now(),
    @ColumnInfo(name="start_time")
    var startTime: LocalTime = LocalTime.now(),
    @ColumnInfo(name="emd_time")
    var endTime: LocalTime = LocalTime.now()
)