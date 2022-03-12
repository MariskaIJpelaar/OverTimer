package org.mariska.overtimer

import android.app.Activity
import android.content.Intent
import android.os.Build.ID
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.allViews
import org.mariska.overtimer.results.WeekHoursContract
import org.mariska.overtimer.weekday.WeekDayAdapter
import org.mariska.overtimer.weekday.WeekDayItem
import java.util.*
import kotlin.collections.ArrayList


class SetHoursActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_weekhours)

        val weekdays = intent.getParcelableArrayExtra(WeekHoursContract.ID).orEmpty().filterIsInstance<WeekDayItem>().toTypedArray()
        val list = findViewById<ListView>(R.id.list)
        val adapter = WeekDayAdapter(this, weekdays)
        list.adapter = adapter

        findViewById<Button>(R.id.submit_button).setOnClickListener {
            val returnedValue = Intent().apply { putExtra(WeekHoursContract.ID, adapter.get_weekdays()) }
            setResult(Activity.RESULT_OK, returnedValue)
            finish()
        }
    }

}