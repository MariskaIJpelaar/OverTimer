package org.mariska.overtimer

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import org.mariska.overtimer.database.*
import org.mariska.overtimer.results.FileSelectContract
import org.mariska.overtimer.results.WeekHoursContract
import org.mariska.overtimer.utils.Logger
import org.mariska.overtimer.weekday.WeekDayItem
import org.mariska.overtimer.weekday.WeekDayManager
import java.io.*
import java.time.DayOfWeek

class MainActivity : AppCompatActivity(), RegisterHoursFragment.RegisterHourDialogListener {
    private var manager: WeekDayManager? = null
    private lateinit var logger: Logger
    private val overTimeViewModel : OverTimerViewModel by viewModels {
        OverTimerViewModelFactory((application as OverTimerApplication).repository)
    }

    private fun refresh() {
        if (manager == null)
            return

        var progress = 100
        //TODO: since totalHours can be calculated from the current_days table
        // --> Also make LiveData and observable!
        val totalHours = manager!!.totalHours()
        if (totalHours != 0)
            progress = (manager!!.getHoursWorked() / totalHours) * 100

        val set = AnimatorSet()
        set.playTogether(
            ObjectAnimator.ofInt(findViewById<ProgressBar>(R.id.hours_progress), "progress", 0, progress),
            ObjectAnimator.ofInt(findViewById<TextView>(R.id.hours_progress_text), "progress_text", 0, progress)
        )
        set.setDuration(2000).start()

        overTimeViewModel.overtime.observe(this) { time ->
            if (time != null)
                findViewById<TextView>(R.id.overtime_num).text = time.toString()
        }
        findViewById<TextView>(R.id.hours_progress_text).text = "$progress%"
        if (overTimeViewModel.overtime.value != null)
            findViewById<TextView>(R.id.overtime_num).text = overTimeViewModel.overtime.value.toString()
        else
            findViewById<TextView>(R.id.overtime_num).text = "0"
    }

    private fun getInternalData() {
        val days = overTimeViewModel.allDays
        days.observe(this) {
            if (it.isEmpty()) {
                manager = WeekDayManager(
                    arrayOf(
                        WeekDayItem(DayOfWeek.MONDAY),
                        WeekDayItem(DayOfWeek.TUESDAY),
                        WeekDayItem(DayOfWeek.WEDNESDAY),
                        WeekDayItem(DayOfWeek.THURSDAY),
                        WeekDayItem(DayOfWeek.FRIDAY),
                        WeekDayItem(DayOfWeek.SATURDAY),
                        WeekDayItem(DayOfWeek.SUNDAY)
                    )
                )
                manager!!.init(overTimeViewModel)
                getContentWeekDays.launch(manager?.getWeekdays())
                refresh()
            } else {
                val array = it.map { day ->
                    val item = WeekDayItem(day.day)
                    item.active = day.active
                    item.date = day.date
                    item.startTime = day.startTime
                    item.endTime = day.endTime
                    item.hoursWorked = day.hoursWorked
                    item
                }.toTypedArray()
                manager = WeekDayManager(array)
                manager!!.init(overTimeViewModel)
                refresh()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logger = Logger(overTimeViewModel)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        findViewById<Button>(R.id.button_register_hours).setOnClickListener {
            RegisterHoursFragment().show(supportFragmentManager, null)
        }

        getInternalData()
        refresh()
    }

    // https://android-developers.googleblog.com/2012/05/using-dialogfragments.html
    override fun onFinishDialog(item : WeekDayItem) {
        manager?.addTime(item)
        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_main, menu)
        return true
    }

    private val getContentWeekDays = registerForActivityResult(WeekHoursContract()) { result ->
        if (result != null) {
            manager?.setWeekdays(result)
            refresh()
        }
    }

    private val getSelectedFile = registerForActivityResult(FileSelectContract()) { result ->
        if (result != null && manager != null) {
            val ostream = FileOutputStream(result.path)
            logger.exportLogs(this, ostream)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_weekhours -> {
            getContentWeekDays.launch(manager?.getWeekdays())
            true
        } R.id.action_export -> {
//            https://stackoverflow.com/questions/49697630/open-file-choose-in-android-app-using-kotlin
            getSelectedFile.launch()
            true
        } else -> {
            super.onOptionsItemSelected(item)
        }
    }
}