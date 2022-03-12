package org.mariska.overtimer

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.mariska.overtimer.weekday.WeekDayAdapter
import org.mariska.overtimer.weekday.WeekDayItem
import java.util.*
import kotlin.collections.ArrayList


class SetHoursActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_weekhours)

        findViewById<Button>(R.id.submit_button).setOnClickListener {
            Toast.makeText(application, "Submit!", Toast.LENGTH_SHORT).show()
        }

        val weekdays : Array<out WeekDayItem> = arrayOf(
            WeekDayItem("Monday"),
            WeekDayItem("Tuesday"),
            WeekDayItem("Wednesday"),
            WeekDayItem("Thursday"),
            WeekDayItem("Friday"),
            WeekDayItem("Saturday"),
            WeekDayItem("Sunday"))
            

        val list = findViewById<ListView>(R.id.list)
        list.adapter = WeekDayAdapter(this, weekdays)
    }

}