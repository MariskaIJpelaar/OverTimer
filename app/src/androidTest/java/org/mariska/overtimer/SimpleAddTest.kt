package org.mariska.overtimer

import android.content.Context
import androidx.annotation.UiThread
import androidx.lifecycle.LifecycleOwner
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mariska.overtimer.database.OverTimerDatabase
import org.mariska.overtimer.database.OverTimerDatabaseDao
import org.mariska.overtimer.utils.observeOnce
import org.mariska.overtimer.weekday.WeekDayItemEntity
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalTime

// from: https://developer.android.com/training/data-storage/room/testing-db
@RunWith(AndroidJUnit4::class)
class SimpleAddTest {
    private lateinit var userDao: OverTimerDatabaseDao
    private lateinit var db: OverTimerDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, OverTimerDatabase::class.java).build()
        userDao = db.overTimerDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun simpleAdd() = runTest {
        val item = WeekDayItemEntity(DayOfWeek.TUESDAY, active = true, endTime = LocalTime.of(17, 0))
        userDao.insert(item)
        val lifecycleOwner: LifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
        // TODO: we cannot observe in a background-thread
        // possible solution: https://medium.com/androiddevelopers/unit-testing-livedata-and-other-common-observability-problems-bb477262eb04
        userDao.getAllActiveDays().observeOnce(lifecycleOwner) { list ->
            assert(list.contains(item))
        }
    }

}