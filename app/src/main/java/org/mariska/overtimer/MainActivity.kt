package org.mariska.overtimer

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.mariska.overtimer.results.WeekHoursContract
import org.mariska.overtimer.results.WeekHoursItemContract
import org.mariska.overtimer.weekday.WeekDayItem
import org.mariska.overtimer.weekday.WeekDayManager


class MainActivity : AppCompatActivity() {
    var manager: WeekDayManager =  WeekDayManager( arrayOf(
        WeekDayItem("Monday"),
        WeekDayItem("Tuesday"),
        WeekDayItem("Wednesday"),
        WeekDayItem("Thursday"),
        WeekDayItem("Friday"),
        WeekDayItem("Saturday"),
        WeekDayItem("Sunday")
    ))

    fun refresh() {
        var progress = 100
        val total_hours = manager.total_hours()
        if (total_hours != 0)
            progress = (manager.workedtime / total_hours) * 100

        val set = AnimatorSet()
        set.playTogether(
            ObjectAnimator.ofInt(findViewById<ProgressBar>(R.id.hours_progress), "progress", 0, progress),
            ObjectAnimator.ofInt(findViewById<TextView>(R.id.hours_progress_text), "progress_text", 0, progress)
        )
        set.setDuration(2000).start()

        findViewById<TextView>(R.id.hours_progress_text).text = "$progress%"
        findViewById<TextView>(R.id.overtime_num).text = manager.overtime.toString()
    }

    private val getContentWeekDayItem = registerForActivityResult(WeekHoursItemContract()) { result ->
        if (result != null)  {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        findViewById<Button>(R.id.button_register_hours).setOnClickListener {
            Toast.makeText(applicationContext, "You clicked register!", Toast.LENGTH_SHORT).show()
        }

        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_main, menu)
        return true
    }

    private val getContentWeekDays = registerForActivityResult(WeekHoursContract()) { result ->
        if (result != null) {
            manager.set_weekdays(result)
            Toast.makeText(this, manager.total_hours().toString(), Toast.LENGTH_SHORT).show()
            refresh()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_weekhours -> {
            getContentWeekDays.launch(manager.get_weekdays())
            true
        } else -> {
            super.onOptionsItemSelected(item)
        }
    }
}