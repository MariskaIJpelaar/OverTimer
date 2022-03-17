package org.mariska.overtimer.weekday

import kotlin.math.max
import kotlin.math.min

class WeekDayManager(days: Array<WeekDayItem>, over_time: Int = 0) {
    var weekdays: Map<String, WeekDayItem> = days.associateBy({it.weekday}, {it})
    var overtime: Int = over_time

    fun get_hours_worked() : Int {
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
        //TODO: rethink: cap should be equal to the sum of all max_days. Perhaps keep track of
        // worked hours per day?
        if (day.active) {
            val max_day = day.total_hours()
            var worked = item.total_hours()
            
            overtime += max(0, day.hours_worked+worked - max_day)
            day.hours_worked = min(max_day, day.hours_worked + worked)
        } else {
            overtime += day.total_hours()
        }
    }

    fun total_hours() : Int {
        return weekdays.map{ it.value.total_hours() }.sum()
    }
}