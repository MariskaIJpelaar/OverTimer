package org.mariska.overtimer.weekday

class WeekDayManager(days: Array<out WeekDayItem>, over_time: Int = 0) {
    val weekdays: Array<out WeekDayItem> = days
    val overtime: Int = over_time

    fun total_hours() : Int {
        return weekdays.sumOf { it.total_hours() }
    }
}