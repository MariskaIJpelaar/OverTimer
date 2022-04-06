package org.mariska.overtimer.weekday

import java.io.Serializable
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.max
import kotlin.math.min

class WeekDayManager(days: Array<WeekDayItem>, over_time: Int = 0) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1311417518
    }

    private var weekdays: Map<String, WeekDayItem> = days.associateBy({it.weekday}, {it})
    var overtime: Int = over_time
    private var weekOfYear: Int = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())

    private fun checkWeek() {
        val current = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        if (current != weekOfYear) {
            weekOfYear = current
            weekdays.forEach { it.value.hoursWorked = 0; }
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
        val day = weekdays[item.weekday] ?: return
        if (day.active) {
            val max = day.totalHours()
            val worked = item.totalHours()

            overtime += max(0, day.hoursWorked+worked - max)
            day.hoursWorked = min(max, day.hoursWorked + worked)
        } else {
            overtime += day.totalHours()
        }
    }

    fun totalHours() : Int {
        return weekdays.map{ it.value.totalHours() }.sum()
    }
}