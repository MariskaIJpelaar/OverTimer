package org.mariska.overtimer.database

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

//https://developer.android.com/codelabs/android-room-with-a-view-kotlin#11
class OverTimerApplication : Application() {
    val database by lazy { OverTimerDatabase.getInstance(this) }
    val repository by lazy { OverTimerRepository(database.overTimerDatabaseDao) }
}