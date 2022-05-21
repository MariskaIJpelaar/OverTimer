package org.mariska.overtimer.weekday

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import kotlinx.coroutines.runBlocking
import org.mariska.overtimer.database.OverTimerViewModel
import org.mariska.overtimer.utils.Logger
import org.mariska.overtimer.utils.observeOnce
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.min
import java.time.temporal.ChronoUnit.HOURS

class WeekDayManager(private var overTimerViewModel: OverTimerViewModel) {
    private var weekOfYear: Int = getWeekOfYear()
    private var logger: Logger = Logger(overTimerViewModel)
    init {
        checkWeek()
    }

    private fun getWeekOfYear() : Int {
        var value: Int = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        // TODO: convert to good type
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

    fun getWeekdays() : LiveData<Map<DayOfWeek, WeekDayItem>> {
        return overTimerViewModel.daysMap
    }

    fun addTime(lifecycleOwner: LifecycleOwner, item : WeekDayItem) {
        overTimerViewModel.daysMap.observeOnce(lifecycleOwner) { data ->
            val day = data[item.weekday]
            if (day != null && day.active) {
                val max = day.totalHours()
                val worked = item.totalHours()
                runBlocking {
                    overTimerViewModel.getHoursWorked(day.date).observeOnce(lifecycleOwner) { range ->
                        val currentOvertime = range.startTime.until(range.endTime, HOURS).toInt() + worked - max
                        if (item.date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) == weekOfYear)
                            overTimerViewModel.update(day.weekday, min(max, day.hoursWorked + worked))
                        logger.log(item.date, item.startTime, item.endTime, item.totalHours())
                    }
                }
            } else {
                logger.log(item.date, item.startTime, item.endTime, item.totalHours())
            }
        }
    }
}