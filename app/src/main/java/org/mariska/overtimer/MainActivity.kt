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
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.mariska.overtimer.results.FileSelectContract
import org.mariska.overtimer.results.WeekHoursContract
import org.mariska.overtimer.results.WeekHoursItemContract
import org.mariska.overtimer.utils.Logger
import org.mariska.overtimer.weekday.WeekDayItem
import org.mariska.overtimer.weekday.WeekDayManager
import java.io.*
import java.util.*

//TODO: consistent variable names!
class MainActivity : AppCompatActivity(), RegisterHoursFragment.RegisterHourDialogListener {
    var manager: WeekDayManager? = null

    val itemsFile: String = "items.ot"

    fun refresh() {
        if (manager == null)
            return

        var progress = 100
        val total_hours = manager!!.total_hours()
        if (total_hours != 0)
            progress = (manager!!.get_hours_worked() / total_hours) * 100

        val set = AnimatorSet()
        set.playTogether(
            ObjectAnimator.ofInt(findViewById<ProgressBar>(R.id.hours_progress), "progress", 0, progress),
            ObjectAnimator.ofInt(findViewById<TextView>(R.id.hours_progress_text), "progress_text", 0, progress)
        )
        set.setDuration(2000).start()

        findViewById<TextView>(R.id.hours_progress_text).text = "$progress%"
        findViewById<TextView>(R.id.overtime_num).text = manager!!.overtime.toString()
    }

    fun getInternalData() {
        // https://www.androidauthority.com/how-to-store-data-locally-in-android-app-717190/
        if (!filesDir.resolve(itemsFile).exists()) {
            manager = WeekDayManager( arrayOf(
                WeekDayItem("Monday"),
                WeekDayItem("Tuesday"),
                WeekDayItem("Wednesday"),
                WeekDayItem("Thursday"),
                WeekDayItem("Friday"),
                WeekDayItem("Saturday"),
                WeekDayItem("Sunday")
            ))
            getContentWeekDays.launch(manager?.get_weekdays())
            return
        }

        // https://stackoverflow.com/questions/57758314/store-custom-kotlin-data-class-to-disk
        val istream = FileInputStream(filesDir.resolve(itemsFile))
        val manager_stream = ObjectInputStream(istream)
        manager = manager_stream.readObject() as? WeekDayManager
        manager_stream.close()
        istream.close()
    }

    fun writeInternalData() {
        if (manager == null)
            return
        val ostream = FileOutputStream(filesDir.resolve(itemsFile))
        val manager_stream = ObjectOutputStream(ostream)
        manager_stream.writeObject(manager)
        manager_stream.close()
        ostream.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        findViewById<Button>(R.id.button_register_hours).setOnClickListener {
            RegisterHoursFragment().show(supportFragmentManager, null)
            writeInternalData()
        }

        getInternalData()
        refresh()
    }

    // https://android-developers.googleblog.com/2012/05/using-dialogfragments.html
    override fun onFinishDialog(item : WeekDayItem) {
        manager?.add_time(item)
        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_main, menu)
        return true
    }

    private val getContentWeekDays = registerForActivityResult(WeekHoursContract()) { result ->
        if (result != null) {
            manager?.set_weekdays(result)
            writeInternalData()
            refresh()
        }
    }

    private val getSelectedFile = registerForActivityResult(FileSelectContract()) { result ->
        if (result != null && manager != null) {
            val ostream = FileOutputStream(result.path)
            Logger.exportLogs(this, ostream)
            ostream.close()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_weekhours -> {
            getContentWeekDays.launch(manager?.get_weekdays())
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