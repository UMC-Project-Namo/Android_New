package com.example.namo.ui.bottom.diary

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.namo.R
import com.example.namo.data.NamoDatabase
import com.example.namo.data.entity.diary.Diary
import com.example.namo.databinding.FragmentDiaryDetailBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat



class DiaryDetailFragment : Fragment() {

    private var _binding: FragmentDiaryDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var db:NamoDatabase

    private lateinit var diary:Diary
    private var longDate:Long = 0
    private var title:String=""
    private var place:String=""
    private var category:Int=0
    private var diaryId=1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDiaryDetailBinding.inflate(inflater, container, false)

        hideBottomNavigation(true)

        db=NamoDatabase.getInstance(requireContext())

        bind()
        charCnt()

        return binding.root
    }

    private fun bind(){
        diaryId=arguments?.getInt("diaryId")!!
        longDate= arguments?.getLong("date")!!
        title = arguments?.getString("title").toString()
        place=arguments?.getString("place").toString()
        category= arguments?.getInt("category")!!

        val formatDate=SimpleDateFormat("yyyy.MM.dd (EE)").format(longDate)
        binding.diaryTodayDayTv.text=SimpleDateFormat("EE").format(longDate)
        binding.diaryTodayNumTv.text=SimpleDateFormat("dd").format(longDate)

        binding.diaryTitleTv.text=title
        binding.diaryInputPlaceTv.text=place
        context?.resources?.let { binding.itemDiaryCategoryColorIv.background.setTint(it.getColor(category)) }
        binding.diaryInputDateTv.text= formatDate
        binding.diaryBackIv.setOnClickListener {
            findNavController().popBackStack()
            hideBottomNavigation(false)
        }
        binding.diaryEditTv.setOnClickListener {
            insertData()
            view?.findNavController()?.navigate(R.id.homeFragment)
            hideBottomNavigation(false)
        }

    }

    private fun insertData(){
        Thread{
            val yearMonth=SimpleDateFormat("yyyy.MM").format(longDate)
            diary=Diary(0, title, longDate, category, binding.diaryContentsEt.text.toString(), listOf(0), yearMonth, place)
            db.diaryDao.insertDiary(diary)
        }.start()  }


    private fun charCnt(){
        with(binding) {
            diaryContentsEt.addTextChangedListener(object : TextWatcher {
                var maxText=""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    maxText=s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(diaryContentsEt.length() > 200){
                        Toast.makeText(requireContext(),"최대 200자까지 입력 가능합니다",
                        Toast.LENGTH_SHORT).show()

                    diaryContentsEt.setText(maxText)
                    diaryContentsEt.setSelection(diaryContentsEt.length())
                    textNumTv.text="${diaryEditTv.length()} / 200"
                } else { textNumTv.text="${diaryEditTv.length()} / 200"}
                }
                override fun afterTextChanged(s: Editable?) {
                }

            })
        }
    }

        private fun hideBottomNavigation( bool : Boolean){
        val bottomNavigationView : BottomNavigationView = requireActivity().findViewById(R.id.nav_bar)
        if(bool) {
            bottomNavigationView.visibility = View.GONE
        } else {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
        hideBottomNavigation(false)
    }
}