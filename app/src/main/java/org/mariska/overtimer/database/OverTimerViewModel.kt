package org.mariska.overtimer.database

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.mariska.overtimer.utils.LogItem
import org.mariska.overtimer.weekday.WeekDayItem
import java.lang.IllegalArgumentException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class OverTimerViewModel(private val repository: OverTimerRepository) : ViewModel() {
    val allLogs = repository.allLogs
    val allDays = repository.allDays
    val allActiveDays = repository.allActiveDays
    val overtime = repository.overtime

    val hoursWorked: LiveData<Int> = Transformations.map(repository.allDays) { data -> data.sumOf { it.hoursWorked }}
    val totalHours: LiveData<Int> = Transformations.map(repository.allDays) { data ->
    data.sumOf { day ->
        val item = WeekDayItem(day.day)
        item.active = day.active
        item.date = day.date
        item.startTime = day.startTime
        item.endTime = day.endTime
        item.hoursWorked = day.hoursWorked
        item.totalHours()
    }}

    fun getAllDays(): Map<DayOfWeek, WeekDayItem> {
        val value = allDays.value ?: return emptyMap()
        return value.map { day ->
            val item = WeekDayItem(day.day)
            item.active = day.active
            item.date = day.date
            item.startTime = day.startTime
            item.endTime = day.endTime
            item.hoursWorked = day.hoursWorked
            item
        }.associateBy({it.weekday}, {it})
    }

    fun insert(item: LogItem) = viewModelScope.launch {
        repository.insert(item)
    }

    fun insert(day: LocalDate, startTime: LocalTime = LocalTime.now(),
               endTime: LocalTime = LocalTime.now(), overtime: Int = 0)  {
        val item = LogItem(day = day, startTime = startTime, endTime = endTime,
            overtime = overtime)
        insert(item)
    }

    fun insert(item: WeekDayItem) = viewModelScope.launch {
        repository.insert(item)
    }

    fun insertAll(days: Array<out WeekDayItem>) = viewModelScope.launch {
        repository.insertAll(days)
    }

    fun update(day: DayOfWeek, hoursWorked: Int) = viewModelScope.launch {
        repository.update(day, hoursWorked)
    }

    fun clearThisWeek() = viewModelScope.launch {
        repository.clearThisWeek()
    }

}

class OverTimerViewModelFactory(private val repository: OverTimerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverTimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OverTimerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}