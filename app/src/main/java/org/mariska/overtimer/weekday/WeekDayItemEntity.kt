package org.mariska.overtimer.weekday

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "recent_week")
data class WeekDayItemEntity(
    @PrimaryKey(autoGenerate = false)
    var day: DayOfWeek = DayOfWeek.MONDAY,
    @ColumnInfo
    var date: LocalDate = LocalDate.now(),
    @ColumnInfo
    var active: Boolean = false,
    @ColumnInfo(name="start_time")
    var startTime: LocalTime = LocalTime.of(9, 0),
    @ColumnInfo(name="end_time")
    var endTime: LocalTime = LocalTime.of(9, 0),
    @ColumnInfo(name="hours_worked")
    var hoursWorked: Int = 0
)
