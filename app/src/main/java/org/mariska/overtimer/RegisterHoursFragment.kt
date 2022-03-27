package org.mariska.overtimer

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.mariska.overtimer.weekday.WeekDayItem
import java.lang.ClassCastException
import java.time.LocalDate
import java.time.LocalTime
import kotlin.IllegalStateException
import java.time.temporal.ChronoUnit.HOURS
import java.time.temporal.WeekFields
import java.util.*


// second answer: https://stackoverflow.com/questions/10905312/receive-result-from-dialogfragment
class RegisterHoursFragment : DialogFragment() {
    public interface RegisterHourDialogListener {
        fun onFinishDialog(item : WeekDayItem)
    }
    private var listener: RegisterHourDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { act ->
            val builder = AlertDialog.Builder(act)
            val view = requireActivity().layoutInflater.inflate(R.layout.dialogue_register_hours, null)

            if (context == null)
                throw IllegalStateException("Context cannot be null")

            var number_edited: Boolean = false;

            // date
            //TODO: month is wrong in DatePickerDialog
            val date_view = view.findViewById<TextView>(R.id.dialogue_date_picker)
            var date = LocalDate.now()
            date_view.text = date.toString()
            date_view.setOnClickListener {
                DatePickerDialog(context!!, { _, year, month, dayOfMonth ->
                    date = LocalDate.of(year, month, dayOfMonth)
                    date_view.text = date.toString()
                }, date.year, date.monthValue, date.dayOfMonth).show()
            }

            // dependencies
            val number_view = view.findViewById<EditText>(R.id.dialogue_work_hours_num)
            val end_time_view = view.findViewById<TextView>(R.id.dialogue_end_time)
            var end_time = LocalTime.parse(end_time_view.text)

            // start-time
            val start_time_view = view.findViewById<TextView>(R.id.dialogue_start_time)
            var start_time = LocalTime.parse(start_time_view.text)
            start_time_view.setOnClickListener {
                TimePickerDialog(context, {_, hourOfDay, minutes ->
                    start_time = LocalTime.of(hourOfDay, minutes)
                    start_time_view.text = start_time.toString()
                    number_view.setText(start_time.until(end_time, HOURS).toString())
                    number_edited = false
                }, start_time.hour, start_time.minute, true).show()
            }

            // end-time
            end_time_view.setOnClickListener {
                TimePickerDialog(context, {_, hourOfDay, minutes ->
                    end_time = LocalTime.of(hourOfDay, minutes)
                    end_time_view.text = start_time.toString()
                    number_view.setText(start_time.until(end_time, HOURS).toString())
                    number_edited = false
                }, end_time.hour, end_time.minute, true).show()
            }

            number_view.setOnFocusChangeListener { _, _ -> number_edited = true }

            builder.setView(view)
                .setPositiveButton("Register") { _, _ ->
                    val weekday = WeekDayItem(date.dayOfWeek.toString().lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
                    weekday.active = true
                    if (number_edited && number_view.text.toString() != "0") {
                        weekday.start_time = LocalTime.of(8, 0)
                        weekday.end_time = start_time.plusHours(
                            Integer.parseInt(number_view.text.toString()).toLong()
                        )
                    } else {
                        weekday.start_time = start_time
                        weekday.end_time = end_time
                    }
                    //TODO: only add the items which belong to this week
                    listener?.onFinishDialog(weekday)
                    //TODO: log items
                    this.dismiss()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    getDialog()?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as RegisterHourDialogListener
        } catch (e : ClassCastException) {
            throw ClassCastException("$context must implement RegisterHourDialogListener")
        }
    }
}