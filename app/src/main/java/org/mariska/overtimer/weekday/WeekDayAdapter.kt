package org.mariska.overtimer.weekday

import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import org.mariska.overtimer.R
import java.lang.RuntimeException
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.coroutines.coroutineContext

// https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
//https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/
class WeekDayAdapter(private val context: Context, private val weekdays: List<WeekDayItem>) :
    RecyclerView.Adapter<WeekDayAdapter.ViewHolder>() {
    fun getWeekDays() : List<WeekDayItem> {
        return weekdays
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: WeekDayItem = weekdays[position]

        // active
        holder.activeView.isChecked = item.active
        holder.activeView.setOnCheckedChangeListener { _, isChecked ->
            item.active = isChecked
        }

        // weekday
        holder.weekDayView.text = item.weekday.toString()

        // start-time
        holder.startView.text = item.startTime.toString()
        holder.startView.setOnClickListener {
            val current = LocalTime.parse(holder.startView.text)
            TimePickerDialog(context, { _, hourOfDay, minutes ->
                holder.startView.text = LocalTime.of(hourOfDay, minutes).toString()
                item.active = true
                holder.activeView.isChecked = true
                item.startTime = LocalTime.of(hourOfDay, minutes)
            }, current.hour, current.minute, true).show()
        }

        // end-time
        holder.endView.text = item.endTime.toString()
        holder.endView.setOnClickListener {
            val current = LocalTime.parse(holder.endView.text)
            TimePickerDialog(context, { _, hourOfDay, minutes ->
                holder.endView.text = LocalTime.of(hourOfDay, minutes).toString()
                item.active = true
                holder.activeView.isChecked = true
                item.endTime = LocalTime.of(hourOfDay, minutes)
            }, current.hour, current.minute, true).show()
        }
    }

    override fun getItemCount(): Int {
        return weekdays.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val imageView: ImageView = itemView.findViewById(R.id.imageview)
//        val textView: TextView = itemView.findViewById(R.id.textView)
        val activeView = itemView.findViewById<CheckBox>(R.id.checkBox)
        val weekDayView = itemView.findViewById<TextView>(R.id.weekday)
        val startView = itemView.findViewById<TextView>(R.id.start_time)
        val endView = itemView.findViewById<TextView>(R.id.end_time)
    }
}