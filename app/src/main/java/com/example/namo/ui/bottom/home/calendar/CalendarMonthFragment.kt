package com.example.namo.ui.bottom.home.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namo.R
import com.example.namo.data.NamoDatabase
import com.example.namo.data.entity.home.Category
import com.example.namo.data.entity.home.Event
import com.example.namo.databinding.FragmentCalendarMonthBinding
import com.example.namo.ui.bottom.diary.mainDiary.DiaryAddFragment
import com.example.namo.ui.bottom.diary.mainDiary.DiaryModifyFragment
import com.example.namo.ui.bottom.home.HomeFragment
import com.example.namo.ui.bottom.home.adapter.DailyGroupRVAdapter
import com.example.namo.ui.bottom.home.adapter.DailyPersonalRVAdapter
import com.example.namo.ui.bottom.home.schedule.ScheduleActivity
import org.joda.time.DateTime

class CalendarMonthFragment : Fragment() {
    lateinit var db : NamoDatabase
    private lateinit var binding : FragmentCalendarMonthBinding
    private lateinit var categoryList : List<Category>

    private var millis : Long = 0L
    var isShow = false
    private lateinit var monthList : List<DateTime>
    private lateinit var tempEvent : List<Event>

    private var prevIdx = -1
    private var nowIdx = 0
    private var event_personal : ArrayList<Event> = arrayListOf()
    private var event_group : ArrayList<Event> = arrayListOf()
    private val personalEventRVAdapter = DailyPersonalRVAdapter()
    private val groupEventRVAdapter = DailyGroupRVAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            millis = it.getLong(MILLIS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarMonthBinding.inflate(inflater, container, false)
        db = NamoDatabase.getInstance(requireContext())

        binding.calendarMonthView.setDayList(millis)
        monthList = binding.calendarMonthView.getDayList()


        binding.homeFab.setOnClickListener {
            Log.d("CalendarMonth", "Dialog open : $nowIdx")
            Log.d("CalendarMonth", "Dialog open : " + monthList[nowIdx].toString())
            val intent = Intent(context, ScheduleActivity::class.java)
            intent.putExtra("nowDay", monthList[nowIdx].millis)
            requireActivity().startActivity(intent)
        }

        binding.calendarMonthView.onDateClickListener = object : CustomCalendarView.OnDateClickListener {
            override fun onDateClick(date: DateTime?, pos : Int?) {
                val prevFragment = HomeFragment.currentFragment as CalendarMonthFragment?
                if (prevFragment != null && prevFragment != this@CalendarMonthFragment) {
                    prevFragment.binding.calendarMonthView.selectedDate = null
                    prevFragment.binding.constraintLayout.transitionToStart()
                }

                HomeFragment.currentFragment = this@CalendarMonthFragment
                HomeFragment.currentSelectedPos = pos
                HomeFragment.currentSelectedDate = date
                Log.d("CalendarMonth", HomeFragment.currentFragment.toString())

                if (date == null) Log.d("CalendarMonth", "The NULL clicked!")
                Log.d("CalendarMonth", "THE $date is clicked!")
                binding.calendarMonthView.selectedDate = date

                if (date != null && pos != null) {
                    nowIdx = pos
                    setDaily(nowIdx)
                    Log.d("CHECK_TODAY", "isShow : $isShow | prevIdx : $prevIdx | nowIdx : $nowIdx")

                    if (isShow && prevIdx == nowIdx) {
                        binding.constraintLayout.transitionToStart()
                        binding.calendarMonthView.selectedDate = null
                        HomeFragment.currentFragment = null
                        HomeFragment.currentSelectedPos = null
                        HomeFragment.currentSelectedDate = null
                    }
                    else if (!isShow) {
                        binding.constraintLayout.transitionToEnd()
                    }
                    isShow = !isShow
                    prevIdx = nowIdx
                }

//                binding.calendarMonthView.invalidate()
            }
        }

        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
//    }

    override fun onResume() {
        super.onResume()
        setAdapter()
        getCategoryList()

        var forDB : Thread = Thread {
            tempEvent = db.eventDao.getEventMonth(monthList[0].withTimeAtStartOfDay().millis, monthList[41].plusDays(1).withTimeAtStartOfDay().millis)
        }
        forDB.start()
        try {
            forDB.join()
        } catch (e : InterruptedException) {
            e.printStackTrace()
        }
        binding.calendarMonthView.setEventList(tempEvent)

//        onBackPressedCallback.isEnabled = true
        Log.d("CalendarMonth", "OnResume")
        Log.d("CalendarMonth", "Day list : " + binding.calendarMonthView.getDayList().toString())
        Log.d("CalendarMonth", "Event list : " + binding.calendarMonthView.getEventList().toString())

        if (HomeFragment.currentFragment == null) {
            return
        }
        else if (this@CalendarMonthFragment != HomeFragment.currentFragment) {
            isShow = false
            prevIdx = -1
        } else {
            binding.calendarMonthView.selectedDate = HomeFragment.currentSelectedDate
            nowIdx = HomeFragment.currentSelectedPos!!
            setDaily(nowIdx)
            binding.constraintLayout.transitionToEnd()
            isShow = true
            prevIdx = nowIdx
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("CalendarMonth", "OnPause")
//        val listener = object : CustomCalendarView.OnDateClickListener {
//            override fun onDateClick(date: DateTime?, pos : Int?) {
//                binding.calendarMonthView.selectedDate = null
//                binding.constraintLayout.transitionToStart()
//                isShow = false
////                binding.calendarMonthView.invalidate()
//            }
//        }
//        listener.onDateClick(null, null)
    }

    private fun setAdapter() {
        binding.homeDailyEventRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.homeDailyEventRv.adapter = personalEventRVAdapter

        binding.homeDailyGroupEventRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.homeDailyGroupEventRv.adapter = groupEventRVAdapter
//        if (nowIdx==0) setToday()

        personalEventRVAdapter.setContentClickListener(object : DailyPersonalRVAdapter.ContentClickListener {
            override fun onContentClick(event: Event) {
                val intent = Intent(context, ScheduleActivity::class.java)
                intent.putExtra("event", event)
                requireActivity().startActivity(intent)
            }

        })

        /** 기록 아이템 클릭 리스너 **/
        personalEventRVAdapter.setRecordClickListener(object : DailyPersonalRVAdapter.DiaryInterface{
            override fun onAddClicked(event: Event) {

                val bundle=Bundle()
                bundle.putSerializable("event",event)

                val diaryFrag= DiaryAddFragment()
                diaryFrag.arguments=bundle

                view?.findNavController()?.navigate(R.id.action_homeFragment_to_diaryDetailFragment2, bundle)
            }
            override fun onEditClicked(event: Event) {

                val bundle=Bundle()
                bundle.putSerializable("event",event)

                val editFrag= DiaryModifyFragment()
                editFrag.arguments=bundle
                view?.findNavController()?.navigate(R.id.action_homeFragment_to_diaryModifyFragment,bundle)
            }
        })
        /** ----- **/
    }

    private fun getCategoryList() {
        val thread = Thread {
            categoryList = db.categoryDao.getCategoryList()
        }
        thread.start()
        try {
            thread.join()
        } catch ( e : InterruptedException) {
            e.printStackTrace()
        }

        binding.calendarMonthView.setCategoryList(categoryList)
    }

    private fun setDaily(idx : Int) {
        binding.homeDailyHeaderTv.text = monthList[idx].toString("MM.dd (E)")
        binding.dailyScrollSv.scrollTo(0,0)
        setData(idx)
    }

    private fun setData(idx : Int) {
        getEvent(idx)

        personalEventRVAdapter.addPersonal(event_personal)
        groupEventRVAdapter.addGroup(event_group)
        setEmptyMsg()
    }

    private fun setEmptyMsg() {
        if (event_personal.size == 0 ) binding.homeDailyEventNoneTv.visibility = View.VISIBLE
        else binding.homeDailyEventNoneTv.visibility = View.GONE

        if (event_group.size == 0) binding.homeDailyGroupEventNoneTv.visibility = View.VISIBLE
        else binding.homeDailyGroupEventNoneTv.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getEvent(idx : Int) {
        event_personal.clear()
        event_group.clear()
        var todayStart = monthList[idx].withTimeAtStartOfDay().millis
        var todayEnd = monthList[idx].plusDays(1).withTimeAtStartOfDay().millis - 1

        var forPersonalEvent : Thread = Thread {
            event_personal = db.eventDao.getEventDaily(todayStart, todayEnd) as ArrayList<Event>
            personalEventRVAdapter.addPersonal(event_personal)
            requireActivity().runOnUiThread {
                Log.d("CalendarMonth", "Personal Event : $event_personal")
                personalEventRVAdapter.notifyDataSetChanged()
            }
        }
        forPersonalEvent.start()

        try {
            forPersonalEvent.join()
        } catch (e : InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val MILLIS = "MILLIS"

        fun newInstance(millis : Long) = CalendarMonthFragment().apply {
            arguments = Bundle().apply {
                putLong(MILLIS, millis)
            }
        }
    }
}