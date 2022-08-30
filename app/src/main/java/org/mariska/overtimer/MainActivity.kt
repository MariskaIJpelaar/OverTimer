package org.mariska.overtimer

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
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
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.mariska.overtimer.database.*
import org.mariska.overtimer.results.FileSelectContract
import org.mariska.overtimer.results.WeekHoursContract
import org.mariska.overtimer.utils.LogItem
import org.mariska.overtimer.utils.Logger
import org.mariska.overtimer.utils.observeOnce
import org.mariska.overtimer.weekday.WeekDayItem
import org.mariska.overtimer.weekday.WeekDayItemEntity
import org.mariska.overtimer.weekday.WeekDayManager
import java.io.*
import java.lang.IllegalStateException
import java.time.DayOfWeek

class MainActivity : AppCompatActivity(), RegisterHoursFragment.RegisterHourDialogListener {
    private lateinit var logger: Logger
    private lateinit var manager: WeekDayManager
    private val overTimeViewModel : OverTimerViewModel by viewModels {
        OverTimerViewModelFactory((application as OverTimerApplication).repository)
    }

    private fun displayProgress(totalHours: Int, hoursWorked: Int) {
        var progress = 100
        if (totalHours != 0)
            progress = ((hoursWorked * 100) / totalHours)

        val progressText = findViewById<TextView>(R.id.hours_progress_text)
        progressText.text = "$progress%"

        val set = AnimatorSet()
        set.playTogether(
            ObjectAnimator.ofInt(findViewById<ProgressBar>(R.id.hours_progress), "progress", 0, progress),
            ObjectAnimator.ofInt(findViewById<TextView>(R.id.hours_progress_text), "progress_text", 0, progress)
        )
        set.setDuration(2000).start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        manager = WeekDayManager(overTimeViewModel, this)
        logger = Logger(overTimeViewModel)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        findViewById<Button>(R.id.button_register_hours).setOnClickListener {
            RegisterHoursFragment().show(supportFragmentManager, null)
        }

        // get weekday-schedule if required
        overTimeViewModel.allDays.observeOnce(this) { days ->
            if (days == null || days.isEmpty()) {
                getContentWeekDays.launch(arrayOf(
                    WeekDayItem(DayOfWeek.MONDAY),
                    WeekDayItem(DayOfWeek.TUESDAY),
                    WeekDayItem(DayOfWeek.WEDNESDAY),
                    WeekDayItem(DayOfWeek.THURSDAY),
                    WeekDayItem(DayOfWeek.FRIDAY),
                    WeekDayItem(DayOfWeek.SATURDAY),
                    WeekDayItem(DayOfWeek.SUNDAY)
                ))
            }
        }

        displayProgress(overTimeViewModel.totalHours.value ?: 0, overTimeViewModel.hoursWorked.value ?: 0)
        overTimeViewModel.totalHours.observe(this) { totalHours ->
            overTimeViewModel.hoursWorked.observeOnce(this) { hoursWorked ->
                displayProgress(totalHours, hoursWorked ?: 0)
            }
        }
        overTimeViewModel.hoursWorked.observe(this) { hoursWorked ->
            overTimeViewModel.totalHours.observeOnce(this) { totalHours ->
                displayProgress(totalHours ?: 0, hoursWorked)
            }
        }

        // Overtime-display
        overTimeViewModel.overtime.observe(this) { time ->
            if (time != null)
                findViewById<TextView>(R.id.overtime_num).text = time.toString()
        }
        if (overTimeViewModel.overtime.value != null)
            findViewById<TextView>(R.id.overtime_num).text = overTimeViewModel.overtime.value.toString()
        else
            findViewById<TextView>(R.id.overtime_num).text = "0"
    }

    // https://android-developers.googleblog.com/2012/05/using-dialogfragments.html
    override fun onFinishDialog(item : WeekDayItem) {
        manager.addTime(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_main, menu)
        return true
    }

    private val getContentWeekDays = registerForActivityResult(WeekHoursContract()) { result ->
        if (result != null) {
            overTimeViewModel.insertAll(result)
        }
    }

    //    https://commonsware.com/blog/2019/10/19/scoped-storage-stories-saf-basics.html
    private suspend fun export(context: Context, source: Uri, items: List<LogItem>) = withContext(Dispatchers.IO) {
        val resolver: ContentResolver = context.contentResolver
        resolver.openOutputStream(source)?.use { stream ->
            logger.exportLogs(stream, items)
        } ?: throw IllegalStateException("could not open $source")
    }

    private val getSelectedFile = registerForActivityResult(FileSelectContract()) { result ->
        if (result != null) {
            overTimeViewModel.allLogs.observeOnce(this) { items ->
                val me = this
                runBlocking { export(me, result, items) }
            }
//            val file = DocumentFile.fromSingleUri(this, result)
//            val ostream = FileOutputStream(file?.name)
//            logger.exportLogs(this, ostream)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_weekhours -> {
            manager.getWeekdays().observeOnce(this) { data ->
                getContentWeekDays.launch(data.values.toTypedArray())
            }
            true
        } R.id.action_export -> {
            // TODO: test
//            https://stackoverflow.com/questions/49697630/open-file-choose-in-android-app-using-kotlin
            getSelectedFile.launch()
            true
        } else -> {
            super.onOptionsItemSelected(item)
        }
    }
}