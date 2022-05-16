package org.mariska.overtimer.splash

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mariska.overtimer.database.OverTimerApplication

class SplashViewModel(application: OverTimerApplication) : AndroidViewModel(application) {
    var liveData = application.repository.allDays

    fun initSplash() {
        viewModelScope.launch {
            delay(2000)
            updateLiveData()
        }
    }

    private fun updateLiveData() {
        //TODO: s.thing?
    }

}