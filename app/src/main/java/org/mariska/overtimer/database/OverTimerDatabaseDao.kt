package org.mariska.overtimer.database

import androidx.lifecycle.LiveData
import androidx.room.*
import org.mariska.overtimer.utils.LogItem
import org.mariska.overtimer.utils.TimeRange
import org.mariska.overtimer.weekday.WeekDayItemEntity
import java.time.DayOfWeek
import java.time.LocalDate

@Dao
interface OverTimerDatabaseDao {
    // NOTE: on conflict, returns -1
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: LogItem): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WeekDayItemEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg item: WeekDayItemEntity)
    @Update
    suspend fun update(item: LogItem)
    @Query("UPDATE recent_week SET hours_worked = :hoursWorked WHERE day = :day ")
    suspend fun update(day: DayOfWeek, hoursWorked: Int)
    @Query("DELETE FROM recent_week")
    suspend fun clearThisWeek()
    @Query("SELECT * FROM logged_times ORDER BY day, start_time, end_time")
    fun getAllLogs(): LiveData<List<LogItem>>
    @Query("SELECT * FROM recent_week")
    fun getAllDays(): LiveData<List<WeekDayItemEntity>>
    @Query("SELECT * FROM recent_week WHERE active == 1")
    fun getAllActiveDays(): LiveData<List<WeekDayItemEntity>>
    @Query("SELECT SUM(over_time) FROM logged_times")
    fun getOvertime(): LiveData<Int>
    @Query("SELECT start_time, end_time FROM logged_times WHERE day = :day")
    fun getHoursWorked(day: LocalDate) : LiveData<TimeRange>
}