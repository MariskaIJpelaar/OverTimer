package org.mariska.overtimer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.mariska.overtimer.results.WeekHoursContract
import org.mariska.overtimer.weekday.WeekDayAdapter
import org.mariska.overtimer.weekday.WeekDayItem

class SetHoursActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_weekhours)

        val weekdays = intent.getParcelableArrayExtra(WeekHoursContract.ID).orEmpty().filterIsInstance<WeekDayItem>().toTypedArray()
        val list = findViewById<RecyclerView>(R.id.list)
        list.layoutManager = LinearLayoutManager(this)
        val adapter = WeekDayAdapter(this, weekdays.toList())
        list.adapter = adapter

        findViewById<Button>(R.id.submit_button).setOnClickListener {
            val returnedValue = Intent().apply { putExtra(WeekHoursContract.ID, adapter.getWeekDays().toTypedArray()) }
            setResult(Activity.RESULT_OK, returnedValue)
            finish()
        }
    }

}