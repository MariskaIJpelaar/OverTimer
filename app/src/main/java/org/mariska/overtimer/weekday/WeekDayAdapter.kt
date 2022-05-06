package org.mariska.overtimer.weekday

import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.mariska.overtimer.R
import java.lang.RuntimeException
import java.time.DayOfWeek
import java.time.LocalTime

// https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
class WeekDayAdapter(context: Context, objects: Array<out WeekDayItem>) :
    ArrayAdapter<WeekDayItem>(context, 0, objects) {
    private val weekdays : Map<DayOfWeek, WeekDayItem> = objects.associateBy({it.weekday}, {it})

    fun getWeekdays() :  Array<out WeekDayItem> { return weekdays.map { it.value }.toTypedArray()}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row, parent, false)
            if (view == null)
                throw RuntimeException("Help, view remains null!")
        }

        val weekDayItem = getItem(position) ?: throw RuntimeException("Help, item is null!")
        val name = weekDayItem.weekday

        // active
        val active = view.findViewById<CheckBox>(R.id.checkBox)
        active.isChecked = weekDayItem.active
        weekdays[name]?.active = weekDayItem.active
        active.setOnCheckedChangeListener { checkbox, isChecked ->
            weekdays[name]?.active = isChecked
            checkbox.isChecked = isChecked
        }

        view.findViewById<TextView>(R.id.weekday).text = weekDayItem.weekday.toString()

        // start - time
        val start = view.findViewById<TextView>(R.id.start_time)
        start.text = weekDayItem.startTime.toString()
        start.setOnClickListener {
            val current = LocalTime.parse(start.text)
            TimePickerDialog(context, { _, hourOfDay, minutes ->
                start.text = LocalTime.of(hourOfDay, minutes).toString()
                active.isChecked = true
                weekdays[name]?.active = true
                weekdays[name]?.startTime = LocalTime.of(hourOfDay, minutes)
            }, current.hour, current.minute, true).show()
        }

        // end - time
        val end = view.findViewById<TextView>(R.id.end_time)
        end.text = weekDayItem.endTime.toString()
        end.setOnClickListener {
            val current = LocalTime.parse(end.text)
            TimePickerDialog(context, { _, hourOfDay, minutes ->
                end.text = LocalTime.of(hourOfDay, minutes).toString()
                active.isChecked = true
                weekdays[name]?.active = true
                weekdays[name]?.endTime = LocalTime.of(hourOfDay, minutes)
            }, current.hour, current.minute, true).show()
        }

        return view
    }
}