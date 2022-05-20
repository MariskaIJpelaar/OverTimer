package org.mariska.overtimer

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import org.mariska.overtimer.utils.Logger
import org.mariska.overtimer.weekday.WeekDayItem
import java.lang.ClassCastException
import java.time.LocalDate
import java.time.LocalTime
import kotlin.IllegalStateException
import java.time.temporal.ChronoUnit.HOURS
import java.util.*


// second answer: https://stackoverflow.com/questions/10905312/receive-result-from-dialogfragment
class RegisterHoursFragment : DialogFragment() {
    interface RegisterHourDialogListener {
        fun onFinishDialog(item : WeekDayItem)
    }
    private var listener: RegisterHourDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { act ->
            val builder = AlertDialog.Builder(act)
            val view = requireActivity().layoutInflater.inflate(R.layout.dialogue_register_hours, null)

            if (context == null)
                throw IllegalStateException("Context cannot be null")

            // date
            val dateView = view.findViewById<TextView>(R.id.dialogue_date_picker)
            var date = LocalDate.now()
            dateView.text = date.toString()
            dateView.setOnClickListener {
                DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                    date = LocalDate.of(year, month+1, dayOfMonth)
                    dateView.text = date.toString()
                }, date.year, date.monthValue-1, date.dayOfMonth).show()
            }

            // dependencies
            val numberView = view.findViewById<EditText>(R.id.dialogue_work_hours_num)
            val endTimeView = view.findViewById<TextView>(R.id.dialogue_end_time)
            var endTime = LocalTime.parse(endTimeView.text)

            // start-time
            val startTimeView = view.findViewById<TextView>(R.id.dialogue_start_time)
            var startTime = LocalTime.parse(startTimeView.text)
            startTimeView.setOnClickListener {
                TimePickerDialog(context, {_, hourOfDay, minutes ->
                    startTime = LocalTime.of(hourOfDay, minutes)
                    startTimeView.text = startTime.toString()
                    numberView.setText(startTime.until(endTime, HOURS).toString())
                }, startTime.hour, startTime.minute, true).show()
            }

            // end-time
            endTimeView.setOnClickListener {
                TimePickerDialog(context, {_, hourOfDay, minutes ->
                    endTime = LocalTime.of(hourOfDay, minutes)
                    endTimeView.text = endTime.toString()
                    numberView.setText(startTime.until(endTime, HOURS).toString())
                }, endTime.hour, endTime.minute, true).show()
            }

            // number
            numberView.setText(startTime.until(endTime, HOURS).toString())
            // TODO: check if start_time + number <= max_day_time
            // TODO: change to finish-text-change?
            numberView.doAfterTextChanged { text ->
                if (text != null && text.toString() != "") {
                    val number: Long = Integer.parseInt(text.toString()).toLong()
                    endTime = startTime.plusHours(number)
                    endTimeView.text = endTime.toString()
                }
            }

            builder.setView(view)
                .setPositiveButton("Register") { _, _ ->
                    val weekday = WeekDayItem(date.dayOfWeek)
                    weekday.active = true
                    weekday.date = date
                    weekday.startTime = startTime
                    weekday.endTime = endTime
                    listener?.onFinishDialog(weekday)
                    this.dismiss()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    dialog?.cancel()
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