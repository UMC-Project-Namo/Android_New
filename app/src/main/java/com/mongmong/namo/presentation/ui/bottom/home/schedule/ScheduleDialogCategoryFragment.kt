package com.mongmong.namo.presentation.ui.bottom.home.schedule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mongmong.namo.R
import com.mongmong.namo.data.local.NamoDatabase
import com.mongmong.namo.presentation.ui.bottom.home.schedule.adapter.DialogCategoryRVAdapter
import com.mongmong.namo.data.local.entity.home.Category
import com.mongmong.namo.data.local.entity.home.Schedule
import com.mongmong.namo.databinding.FragmentScheduleDialogCategoryBinding
import com.mongmong.namo.presentation.ui.bottom.home.category.CategoryActivity

class ScheduleDialogCategoryFragment : Fragment() {

    private lateinit var binding : FragmentScheduleDialogCategoryBinding
    private lateinit var db: NamoDatabase
    private val args : ScheduleDialogCategoryFragmentArgs by navArgs()

    private var event : Schedule = Schedule()

    private lateinit var categoryRVAdapter : DialogCategoryRVAdapter
    private var categoryList : List<Category> = arrayListOf()
    private var initCategory : Int = 0
    private var selectedCategory : Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScheduleDialogCategoryBinding.inflate(inflater, container, false)
        Log.d("DIALOG_CATEGORY", "카테고리 다이얼로그")

        db = NamoDatabase.getInstance(requireContext())
        event = args.event

        selectedCategory = event.categoryId

        onClickCategoryEdit()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getCategoryList()
    }

    private fun getCategoryList() {
        val rv = binding.dialogScheduleCategoryRv

        val r = Runnable {
            try {
                categoryList = db.categoryDao.getActiveCategoryList(true)
                categoryRVAdapter = DialogCategoryRVAdapter(requireContext(), categoryList)
                Log.d("TEST_CATEGORY", "Category dialog : $categoryList")
                categoryRVAdapter.setSelectedIdx(selectedCategory)
                categoryRVAdapter.setMyItemClickListener(object: DialogCategoryRVAdapter.MyItemClickListener {
                    // 아이템 클릭
                    override fun onSendIdx(category: Category) {
                        // 카테고리 세팅
                        event.categoryId = category.categoryId
                        event.categoryServerId = category.serverId
                        Log.d("TEST_CATEGORY", "In category : ${event.categoryId}")
                        Log.d("TEST_CATEGORY", "In category Server: ${event.categoryServerId}")
                        Log.d("TEST_CATEGORY", "In category Result: ${event}")
                        val action = ScheduleDialogCategoryFragmentDirections.actionScheduleDialogCategoryFragmentToScheduleDialogBasicFragment(event)
                        findNavController().navigate(action)
                    }
                })
                // 활성화 상태인 리스트만 보줌
                categoryList = db.categoryDao.getActiveCategoryList(true)
                requireActivity().runOnUiThread {
                    rv.adapter = categoryRVAdapter
                    rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
                Log.d("ScheduleDialogFrag", "categoryDao: ${db.categoryDao.getCategoryList()}")
            } catch (e: Exception) {
                Log.d("schedule category", "Error - $e")
            }
        }

        val thread = Thread(r)
        thread.start()
        try {
            thread.join()
        } catch (e : InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun onClickCategoryEdit()  {
        binding.dialogScheduleCategoryEditCv.setOnClickListener {
            Log.d("DialogCategoryFrag", "categoryEditCV 클릭")
            startActivity(Intent(activity, CategoryActivity::class.java))
        }
    }
}