package com.highresults.crimesummary

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.util.*

private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val REQUEST_TIME = 1

class CrimeFragment: Fragment(R.layout.fragment_crime),DatePickerFragment.Callbacks,TimePickerFragment.Callbacks {

    companion object{
        fun newInstance(Id: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID,Id)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId:UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        titleField = view?.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date)
        timeButton = view.findViewById(R.id.crime_time)
        solvedCheckBox = view.findViewById(R.id.crime_solved)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner, { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            })
    }

    private fun updateUI() {
        titleField.setText(crime.title)

        val calendar = Calendar.getInstance()
        calendar.time = crime.date
        val locale: Locale = Locale.getDefault()
        var patternDate: String? = "ddMMMyyyy"
        var patternTime: String? = "Hms"
        patternDate = DateFormat.getBestDateTimePattern(locale, patternDate)
        patternTime = DateFormat.getBestDateTimePattern(locale, patternTime)
        dateButton.text = DateFormat.format( patternDate, crime.date)
        timeButton.text = DateFormat.format( patternTime, crime.date)
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }


    override fun onStart() {
        super.onStart()
        val titleWatcher = object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        titleField.addTextChangedListener(titleWatcher)
        solvedCheckBox.setOnCheckedChangeListener{ _ , isChecked ->
            crime.isSolved = isChecked
        }
        dateButton.setOnClickListener{
            DatePickerFragment.newInstanse(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
               val trans = this@CrimeFragment.parentFragmentManager.beginTransaction()
                show(trans, DIALOG_DATE)
                //show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
            }
        }
        timeButton.setOnClickListener{
            TimePickerFragment.newInstanse(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_TIME)
                val trans = this@CrimeFragment.parentFragmentManager.beginTransaction()
                show(trans, DIALOG_DATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDateSelected(date: Date) {
        val calendarSelectedDate = Calendar.getInstance()
        calendarSelectedDate.time = date
        val calendar = Calendar.getInstance()
        calendar.time = crime.date
        calendar.set(Calendar.YEAR,calendarSelectedDate.get(Calendar.YEAR))
        calendar.set(Calendar.MONTH,calendarSelectedDate.get(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH,calendarSelectedDate.get(Calendar.DAY_OF_MONTH))
        crime.date = calendar.time
        updateUI()
    }

    override fun onTimeSelected(selectedTime: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = crime.date
        calendar.set(Calendar.HOUR_OF_DAY,0)
        calendar.set(Calendar.MINUTE,0)
        calendar.add(Calendar.MILLISECOND, selectedTime.time.toInt())
        crime.date = calendar.time
        updateUI()
    }

}


