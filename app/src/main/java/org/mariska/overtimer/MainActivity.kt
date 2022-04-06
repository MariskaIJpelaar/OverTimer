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
import org.mariska.overtimer.results.FileSelectContract
import org.mariska.overtimer.results.WeekHoursContract
import org.mariska.overtimer.utils.Logger
import org.mariska.overtimer.weekday.WeekDayItem
import org.mariska.overtimer.weekday.WeekDayManager
import java.io.*

class MainActivity : AppCompatActivity(), RegisterHoursFragment.RegisterHourDialogListener {
    private var manager: WeekDayManager? = null

    private val itemsFile: String = "items.ot"

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
            getContentWeekDays.launch(manager?.getWeekdays())
            return
        }

        // https://stackoverflow.com/questions/57758314/store-custom-kotlin-data-class-to-disk
        val istream = FileInputStream(filesDir.resolve(itemsFile))
        val managerStream = ObjectInputStream(istream)
        manager = managerStream.readObject() as? WeekDayManager
        managerStream.close()
        istream.close()
    }

    private fun writeInternalData() {
        if (manager == null)
            return
        val ostream = FileOutputStream(filesDir.resolve(itemsFile))
        val managerStream = ObjectOutputStream(ostream)
        managerStream.writeObject(manager)
        managerStream.close()
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