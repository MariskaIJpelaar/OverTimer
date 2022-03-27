package org.mariska.overtimer.weekday

import java.io.Serializable
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.max
import kotlin.math.min

class WeekDayManager(days: Array<WeekDayItem>, over_time: Int = 0) : Serializable {
    var weekdays: Map<String, WeekDayItem> = days.associateBy({it.weekday}, {it})
    var overtime: Int = over_time
    var weekOfYear: Int = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())

    fun check_week() {
        val current = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        if (current != weekOfYear) {
            weekOfYear = current
            weekdays.forEach { it.value.hours_worked = 0; }
        }
    }

    fun get_hours_worked() : Int {
        check_week()
        return weekdays.map { it.value.hours_worked }.sum()
    }

    fun set_weekdays(days: Array<out WeekDayItem>) {
        weekdays = days.associateBy({it.weekday}, {it})
    }

    fun get_weekdays() : Array<out WeekDayItem> {
        return weekdays.values.toTypedArray()
    }

    fun add_time(item : WeekDayItem) {
        val day = weekdays[item.weekday] ?: return
        if (day.active) {
            val max = day.total_hours()
            var worked = item.total_hours()

            overtime += max(0, day.hours_worked+worked - max)
            day.hours_worked = min(max, day.hours_worked + worked)
        } else {
            overtime += day.total_hours()
        }
    }

    fun total_hours() : Int {
        return weekdays.map{ it.value.total_hours() }.sum()
    }
}