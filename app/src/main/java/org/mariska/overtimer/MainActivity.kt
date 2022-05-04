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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import org.mariska.overtimer.database.OverTimerDatabase
import org.mariska.overtimer.database.OverTimerDatabaseDao
import org.mariska.overtimer.results.FileSelectContract
import org.mariska.overtimer.results.WeekHoursContract
import org.mariska.overtimer.utils.Logger
import org.mariska.overtimer.weekday.WeekDayItem
import org.mariska.overtimer.weekday.WeekDayManager
import java.io.*
import java.time.DayOfWeek

class MainActivity : AppCompatActivity(), RegisterHoursFragment.RegisterHourDialogListener {
    private var manager: WeekDayManager? = null
    private lateinit var overTimerDao: OverTimerDatabaseDao
    private lateinit var logger: Logger

    private fun refresh() {
        if (manager == null)
            return

        var progress = 100
        val totalHours = manager!!.totalHours()
        if (totalHours != 0)
            progress = (manager!!.getHoursWorked() / totalHours) * 100

        val set = AnimatorSet()
        set.playTogether(
            ObjectAnimator.ofInt(findViewById<ProgressBar>(R.id.hours_progress), "progress", 0, progress),
            ObjectAnimator.ofInt(findViewById<TextView>(R.id.hours_progress_text), "progress_text", 0, progress)
        )
        set.setDuration(2000).start()

        findViewById<TextView>(R.id.hours_progress_text).text = "$progress%"
        findViewById<TextView>(R.id.overtime_num).text = manager!!.overtime.toString()
    }

    private fun getInternalData() {
        val days = overTimerDao.getAllDays()
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
                manager!!.init(overTimerDao, this)
                getContentWeekDays.launch(manager?.getWeekdays())
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
                manager!!.init(overTimerDao, this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        overTimerDao = OverTimerDatabase.getInstance(this).overTimerDatabaseDao
        logger = Logger(overTimerDao)

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