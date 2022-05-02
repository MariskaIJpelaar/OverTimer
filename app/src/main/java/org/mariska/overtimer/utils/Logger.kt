package org.mariska.overtimer.utils

import androidx.lifecycle.LifecycleOwner
import org.mariska.overtimer.database.OverTimerDatabaseDao
import java.io.*
import java.time.LocalDate
import java.time.LocalTime

class Logger {
    companion object {
        private const val format: String = "%s %s-%s, overtime: %s"

        fun log(overTimerDao: OverTimerDatabaseDao, date: LocalDate, start: LocalTime, end: LocalTime, overtime: Int) {
            val item = LogItem(day = date, startTime = start, endTime = end, overtime = overtime)
            if (overTimerDao.insert(item) == -1L)
                overTimerDao.update(item)
        }

        private fun toString(logItem: LogItem) : String {
            return format.format(logItem.day.toString(), logItem.startTime.toString(), logItem.endTime.toString(), logItem.overtime.toString())
        }

        fun exportLogs(overTimerDao: OverTimerDatabaseDao, owner: LifecycleOwner, oStream: FileOutputStream) {
            val logWriter = ObjectOutputStream(oStream)

            val logs = overTimerDao.getAllLogs()
            logs.observe(owner) { items ->
                items?.forEach { logWriter.writeUTF(toString(it)) }
            }
            logWriter.close()
        }
    }
}