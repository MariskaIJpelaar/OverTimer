package org.mariska.overtimer.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.mariska.overtimer.database.OverTimerDatabaseDao
import org.mariska.overtimer.database.OverTimerViewModel
import java.io.*
import java.time.LocalDate
import java.time.LocalTime

class Logger(private val overTimerViewModel: OverTimerViewModel) {
    companion object {
        private const val format: String = "%s %s-%s, overtime: %s"
    }

    fun log(date: LocalDate, start: LocalTime, end: LocalTime, overtime: Int) {
        overTimerViewModel.insert(day = date, startTime = start, endTime = end, overtime = overtime)
    }

    private fun toString(logItem: LogItem) : String {
        return format.format(logItem.day.toString(), logItem.startTime.toString(), logItem.endTime.toString(), logItem.overtime.toString())
    }

    fun exportLogs( owner: LifecycleOwner, oStream: OutputStream) {
        val logWriter = ObjectOutputStream(oStream)

        // TODO: move the observe to MainActivity
        overTimerViewModel.allLogs.observe(owner) { items ->
            items?.forEach { logWriter.writeUTF(toString(it)) }
            oStream.close()
            logWriter.close()
        }
    }
}