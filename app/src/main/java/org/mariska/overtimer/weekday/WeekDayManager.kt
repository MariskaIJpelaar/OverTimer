package org.mariska.overtimer.weekday

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.mariska.overtimer.database.OverTimerDatabaseDao
import org.mariska.overtimer.database.OverTimerViewModel
import org.mariska.overtimer.utils.Logger
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.max
import kotlin.math.min

// TODO: check which functions can be pushdowned to ROOM
class WeekDayManager(private var overTimerViewModel: OverTimerViewModel) {
    private var weekdays: Map<DayOfWeek, WeekDayItem> = overTimerViewModel.getAllDays()
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
//            weekdays.forEach { it.value.hoursWorked = 0; }
            overTimerViewModel.clearThisWeek()
        }
    }

    fun getHoursWorked() : Int {
        return overTimerViewModel.allDays.value?.sumOf { it.hoursWorked } ?: 0;

    }

//    fun setWeekdays(days: Array<out WeekDayItem>) {
//        weekdays = days.associateBy({it.weekday}, {it})
//        overTimerViewModel.insertAll(weekdays.values.toTypedArray())
//    }

    fun getWeekdays() : Array<out WeekDayItem> {
        return weekdays.values.toTypedArray()
    }

    fun addTime(item : WeekDayItem) {
        val day = overTimerViewModel.getAllDays()[item.weekday]
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
        logger.log(item.date, item.startTime, item.endTime, currentOvertime)
    }

    fun totalHours() : Int {
        return weekdays.map{ it.value.totalHours() }.sum()
    }
}