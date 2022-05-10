package org.mariska.overtimer.weekday

import org.mariska.overtimer.database.OverTimerViewModel
import org.mariska.overtimer.utils.Logger
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.max
import kotlin.math.min

class WeekDayManager(private var overTimerViewModel: OverTimerViewModel) {
    private var weekOfYear: Int = getWeekOfYear()
    private var logger: Logger = Logger(overTimerViewModel)
    init {
        checkWeek()
    }

    private fun getWeekOfYear() : Int {
        var value: Int = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        overTimerViewModel.allActiveDays.value?.firstNotNullOf { item ->
            value = item.date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        }
        return value
    }

    private fun checkWeek() {
        val current = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        if (current != weekOfYear) {
            weekOfYear = current
            overTimerViewModel.clearThisWeek()
        }
    }

    fun getWeekdays() : Array<out WeekDayItem> {
        return overTimerViewModel.getAllDays().values.toTypedArray()
    }

    fun addTime(item : WeekDayItem) {
        val day = overTimerViewModel.getAllDays()[item.weekday]
        val currentOvertime: Int
        if (day != null && day.active) {
            val max = day.totalHours()
            val worked = item.totalHours()

            currentOvertime = max(0, day.hoursWorked+worked - max)
            if (day.date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) == weekOfYear)
                overTimerViewModel.update(day.weekday, min(max, day.hoursWorked + worked))
        } else {
            currentOvertime = item.totalHours()
        }
        logger.log(item.date, item.startTime, item.endTime, currentOvertime)
    }
}