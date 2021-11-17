package com.highresults.crimesummary


import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "CrimeListFragment"


class CrimeListFragment: Fragment(R.layout.fragment_crime_list) {
    companion object{
        fun newInstance(): CrimeListFragment{
            return CrimeListFragment()
        }
    }

    interface Callbacks{
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callback: Callbacks? = null
    private lateinit var crimeRecyclerView: RecyclerView


    private val crimeListViewModel: CrimeListViewModel by lazy{
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        crimeRecyclerView = view?.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.recycledViewPool.setMaxRecycledViews(R.layout.list_item_crime, 2)
        crimeRecyclerView.recycledViewPool.setMaxRecycledViews(R.layout.list_item_crime_police, 3)
        crimeRecyclerView.adapter = CrimeAdapter()
        return view
    }

   /* private fun updateUI(crimes: List<Crime>){
        adapter = CrimeAdapter(crimes)
        //crimeRecyclerView.adapter = adapter
        adapter!!.submitList(crimes)
    }*/

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    (crimeRecyclerView.adapter as CrimeAdapter).submitList(crimes)
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return  when(item.itemId){
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callback?.onCrimeSelected(crime.id)
            true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener {
        private lateinit var crime: Crime
        private val titleTextView: TextView =
            itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView =
            itemView.findViewById(R.id.crime_date)
        private val isSolvedImageView:ImageView =
                itemView.findViewById(R.id.is_solved)
        init{
            itemView.setOnClickListener(this)
        }


        fun bind(crime: Crime)
        {
            this.crime = crime
            titleTextView.text = this.crime.title

            val locale: Locale = Locale.getDefault()
            var pattern: String? = "EEEEddMMMyyyy"
            pattern =
                DateFormat.getBestDateTimePattern(locale, pattern)
           // val formatter = SimpleDateFormat(pattern, locale)
            dateTextView.text =  DateFormat.format( pattern,this.crime.date)


            isSolvedImageView.visibility = if(crime.isSolved)
                    View.VISIBLE
                else
                    View.GONE
        }

        override fun onClick(v: View?) {
            callback?.onCrimeSelected(crime.id)
        }
    }

    private inner class CrimeAdapter: ListAdapter<Crime, CrimeHolder>(DiffCallback()){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(viewType, parent, false)
            return CrimeHolder(view)
        }



        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = getItem(position)
            holder.bind(crime)
        }



        override fun getItemViewType(position: Int): Int {
            //val crime = getItem(position)
           // if(crime.requiresPolice)
           //     return R.layout.list_item_crime_police
           // else
                return R.layout.list_item_crime
        }
    }

    private inner class DiffCallback: DiffUtil.ItemCallback<Crime>() {

        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }
    }
}