package org.mariska.overtimer.weekday

import kotlin.math.max
import kotlin.math.min

class WeekDayManager(days: Array<WeekDayItem>, over_time: Int = 0, worked_time: Int = 0) {
    var weekdays: Map<String, WeekDayItem> = days.associateBy({it.weekday}, {it})
    var workedtime: Int = worked_time
    var overtime: Int = over_time

    fun set_weekdays(days: Array<out WeekDayItem>) {
        weekdays = days.associateBy({it.weekday}, {it})
    }

    fun get_weekdays() : Array<out WeekDayItem> {
        return weekdays.values.toTypedArray()
    }

    fun add_time(item : WeekDayItem) {
        val day = weekdays[item.weekday] ?: return
        val cap = total_hours()
        //TODO: rethink
        if (day.active) {
            workedtime = min(cap, workedtime + day.total_hours())
            overtime += max(0, workedtime + day.total_hours() - cap)
        } else {
            overtime += day.total_hours()
        }
    }

    fun total_hours() : Int {
        return weekdays.map{ it.value.total_hours() }.sum()
    }
}