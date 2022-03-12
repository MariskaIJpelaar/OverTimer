package org.mariska.overtimer.weekday

class WeekDayManager(days: Array<out WeekDayItem>, over_time: Int = 0, worked_time: Int = 0) {
    var weekdays: Array<out WeekDayItem> = days
    var workedtime: Int = worked_time
    var overtime: Int = over_time

    fun total_hours() : Int {
        return weekdays.sumOf { it.total_hours() }
    }
}