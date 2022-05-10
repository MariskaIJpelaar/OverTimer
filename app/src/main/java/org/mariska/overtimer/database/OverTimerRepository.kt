package org.mariska.overtimer.database

import androidx.annotation.WorkerThread
import kotlinx.serialization.descriptors.PrimitiveKind
import org.mariska.overtimer.utils.LogItem
import org.mariska.overtimer.weekday.WeekDayItem
import org.mariska.overtimer.weekday.WeekDayItemEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

// https://developer.android.com/codelabs/android-room-with-a-view-kotlin#8

class OverTimerRepository(private val overTimerDatabaseDao: OverTimerDatabaseDao) {
    val allLogs = overTimerDatabaseDao.getAllLogs()
    val allDays = overTimerDatabaseDao.getAllDays()
    val allActiveDays = overTimerDatabaseDao.getAllActiveDays()
    val overtime = overTimerDatabaseDao.getOvertime()



    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: LogItem) {
        if (overTimerDatabaseDao.insert(item) == -1L)
            overTimerDatabaseDao.update(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: WeekDayItem) {
        overTimerDatabaseDao.insert(WeekDayItemEntity(
            day = item.weekday,
            date = item.date,
            active = item.active,
            startTime = item.startTime,
            endTime = item.endTime,
            hoursWorked = item.hoursWorked
        ))
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(days: Array<out WeekDayItem>) {
        overTimerDatabaseDao.insertAll(
            *days.map { value -> WeekDayItemEntity(
                day = value.weekday,
                date = value.date,
                active = value.active,
                startTime = value.startTime,
                endTime = value.endTime,
                hoursWorked = value.hoursWorked
            ) }.toTypedArray()
        )
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(day: DayOfWeek, hoursWorked: Int ) {
        overTimerDatabaseDao.update(day, hoursWorked)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun clearThisWeek() {
        overTimerDatabaseDao.clearThisWeek()
    }

}