package org.mariska.overtimer.database

import androidx.lifecycle.LiveData
import androidx.room.*
import org.mariska.overtimer.utils.LogItem
import org.mariska.overtimer.weekday.WeekDayItemEntity

@Dao
interface OverTimerDatabaseDao {
    // NOTE: on conflict, returns -1
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: LogItem): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: WeekDayItemEntity)
    @Update
    fun update(item: LogItem)
    @Query("DELETE FROM recent_week")
    fun clearThisWeek()
    @Query("SELECT * FROM logged_times ORDER BY day, start_time, end_time")
    fun getAllLogs(): LiveData<List<LogItem>>
    @Query("SELECT * FROM recent_week")
    fun getAllDays(): LiveData<List<WeekDayItemEntity>>
    @Query("SELECT SUM(over_time) FROM logged_times")
    fun getOvertime(): LiveData<Int>
}