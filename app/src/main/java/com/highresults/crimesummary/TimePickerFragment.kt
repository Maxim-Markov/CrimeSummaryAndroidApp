package com.highresults.crimesummary


import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_TIME = "time"

class TimePickerFragment : DialogFragment() {
    companion object {
        fun newInstanse(time: Date): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TIME, time)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }

    interface Callbacks {
        fun onTimeSelected(selectedTime: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = 0
            calendar.add(Calendar.HOUR, hour)
            calendar.add(Calendar.MINUTE, minute)
            val resultTime: Date = calendar.time
            targetFragment?.let { fragment ->
                (fragment as Callbacks).onTimeSelected(resultTime)
            }
        }

        val date = arguments?.getSerializable(ARG_TIME) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(requireContext(), listener, initialHour, initialMinute, true)
    }
}