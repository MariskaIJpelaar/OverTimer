package org.mariska.overtimer.database

import androidx.lifecycle.LiveData
import androidx.room.*
import org.mariska.overtimer.utils.LogItem
import org.mariska.overtimer.weekday.WeekDayItemEntity

@Dao
interface OverTimerDatabaseDao {
    @Insert
    fun insert(item: LogItem)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: WeekDayItemEntity)
    @Update
    fun update(item: LogItem)
    @Query("SELECT * FROM logged_times")
    fun getAllLogs(): LiveData<List<LogItem>>
    @Query("SELECT * FROM recent_week")
    fun getAllDays(): LiveData<List<WeekDayItemEntity>>
}