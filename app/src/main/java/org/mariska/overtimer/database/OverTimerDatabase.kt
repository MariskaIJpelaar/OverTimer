package org.mariska.overtimer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.mariska.overtimer.utils.LogItem
import org.mariska.overtimer.weekday.WeekDayItemEntity

@Database(entities = [LogItem::class, WeekDayItemEntity::class], version = 1, exportSchema = true)
@TypeConverters(OverTimerConverters::class)
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