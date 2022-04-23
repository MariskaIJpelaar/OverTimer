package org.mariska.overtimer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.mariska.overtimer.utils.LogItem

@Database(entities = [LogItem::class], version = 1, exportSchema = true)
abstract class OverTimerDatabase : RoomDatabase() {
    abstract val overTimerDatabaseDao: OverTimerDatabaseDao
    companion object {
        @Volatile
        private var INSTANCE: OverTimerDatabase? = null

        fun getInstance(context: Context): OverTimerDatabase { synchronized(this) {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    OverTimerDatabase::class.java,
                    "logged_times_database").fallbackToDestructiveMigration().build()
                INSTANCE = instance
            }
            return instance
        }}
    }
}