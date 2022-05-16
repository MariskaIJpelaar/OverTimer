package org.mariska.overtimer

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.mariska.overtimer.database.OverTimerApplication
import org.mariska.overtimer.splash.SplashViewModel

class SplashActivity : AppCompatActivity() {
    private lateinit var splashViewModel: SplashViewModel
    //TODO: idk something with getting OverTimerApplication?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.splash_screen)
        initViewModel()
        observeSplashLiveData()
    }

    private fun initViewModel() {
        splashViewModel = ViewModelProvider(this)[SplashViewModel::class.java]
    }

    private fun observeSplashLiveData() {
        splashViewModel.initSplash()
        splashViewModel.liveData.observe(this) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}