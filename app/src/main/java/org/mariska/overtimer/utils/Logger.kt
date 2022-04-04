package org.mariska.overtimer.utils

import android.content.ContextWrapper
import java.io.*
import java.time.LocalDate
import java.time.LocalTime

class Logger {
    companion object {
        val internalFile: String = "logs.log"
        val format: String = "%s %s %s"

        fun log(context: ContextWrapper, date: LocalDate, start: LocalTime, end: LocalTime) {
            val ostream = FileOutputStream(context.filesDir.resolve(internalFile))
            val logWriter = ObjectOutputStream(ostream)
            val content: String = String.format(format, date.toString(), start, end)
            logWriter.writeBytes(content)
            logWriter.close()
            ostream.close()
        }
        fun exportLogs(context: ContextWrapper, oStream: FileOutputStream) {
            val logWriter = ObjectOutputStream(oStream)
            val istream = FileInputStream(context.filesDir.resolve(internalFile))
            val logReader = ObjectInputStream(istream)
            logWriter.writeBytes(logReader.readBytes().toString())
            logReader.close()
            logWriter.close()
        }
    }
}