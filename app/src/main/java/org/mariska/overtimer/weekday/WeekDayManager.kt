package org.mariska.overtimer.weekday

import androidx.lifecycle.LifecycleOwner
import org.mariska.overtimer.database.OverTimerDatabaseDao
import org.mariska.overtimer.utils.Logger
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.max
import kotlin.math.min

class WeekDayManager(days: Array<WeekDayItem>) {
    private var weekdays: Map<DayOfWeek, WeekDayItem> = days.associateBy({it.weekday}, {it})
    private lateinit var overTimerDao: OverTimerDatabaseDao
    var overtime: Int? = null
    private var weekOfYear: Int = getWeekOfYear()
    private lateinit var logger: Logger

    fun init(overTimerDatabaseDao: OverTimerDatabaseDao, owner: LifecycleOwner) {
        overTimerDao = overTimerDatabaseDao
        overTimerDao.getOvertime().observe(owner) { result ->
            overtime = result ?: 0
        }
        logger = Logger(overTimerDao)
    }

    private fun getWeekOfYear() : Int {
        var value: Int = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        weekdays.filter { it.value.active }.firstNotNullOfOrNull { item ->
            value = item.value.date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        }
        return value
    }

    private fun checkWeek() {
        val current = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        if (current != weekOfYear) {
            weekOfYear = current
            weekdays.forEach { it.value.hoursWorked = 0; }
            overTimerDao.clearThisWeek()
        }
    }

    fun getHoursWorked() : Int {
        checkWeek()
        return weekdays.map { it.value.hoursWorked }.sum()
    }

    fun setWeekdays(days: Array<out WeekDayItem>) {
        weekdays = days.associateBy({it.weekday}, {it})
    }

    fun getWeekdays() : Array<out WeekDayItem> {
        return weekdays.values.toTypedArray()
    }

    fun addTime(item : WeekDayItem) {
        val day = weekdays[item.weekday]
        val currentOvertime: Int
        if (day != null && day.active) {
            val max = day.totalHours()
            val worked = item.totalHours()

            currentOvertime = max(0, day.hoursWorked+worked - max)
            if (day.date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) == weekOfYear)
                day.hoursWorked = min(max, day.hoursWorked + worked)
        } else {
            currentOvertime = item.totalHours()
        }
        overtime = overtime?.plus(currentOvertime)
        logger.log(item.date, item.startTime, item.endTime, currentOvertime)
    }

    fun totalHours() : Int {
        return weekdays.map{ it.value.totalHours() }.sum()
    }
}