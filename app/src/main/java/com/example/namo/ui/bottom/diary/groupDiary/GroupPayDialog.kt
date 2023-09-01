package com.example.namo.ui.bottom.diary.groupDiary

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.namo.data.entity.diary.DiaryGroupEvent
import com.example.namo.data.remote.diary.DiaryResponse
import com.example.namo.databinding.DialogGroupPayBinding
import com.example.namo.ui.bottom.diary.groupDiary.adapter.GroupPayMemberRVAdapter
import java.lang.Boolean.TRUE


class GroupPayDialog(
    private var placeMember: List<DiaryResponse.GroupUser>,
    private var placeEvent: DiaryGroupEvent,
    private val pay: (Int) -> Unit,
    private val checkedMember: (List<Int>) -> Unit
) : DialogFragment(), View.OnClickListener {  // 그룹 다이어리 장소별 정산 다이얼로그

    lateinit var binding: DialogGroupPayBinding
    private lateinit var payMemberRVAdapter: GroupPayMemberRVAdapter

    private var memberIsChecked = mutableListOf<Pair<Int, Boolean>>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogGroupPayBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  //배경 투명하게
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)  //dialog 모서리 둥글게

        val memberIntList = placeMember.map { it.userId }

        memberIsChecked.addAll(memberIntList.map { userId ->
            userId to false
        })

        account()
        onClickListener()


        binding.groupPayTotalEt.isSingleLine = true
        binding.groupPayTotalEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateResultText()
            }

            override fun afterTextChanged(s: Editable?) {
                updateResultText()
            }
        })

        if (placeEvent.pay != 0) {
            val memberSize = placeEvent.members.size
            binding.groupPayCountTv.text = memberSize.toString()
            binding.groupPayTotalEt.setText(placeEvent.pay.toString())
            updateResultText()

            for (i in placeEvent.members) {
                val index = memberIsChecked.indexOfFirst { it.first == i }
                if (index != -1) {
                    memberIsChecked[index] = i to true
                }
            }
        }

        return binding.root
    }


    private fun account() {

        payMemberRVAdapter = GroupPayMemberRVAdapter(placeMember, memberIsChecked)
        binding.groupPayPersonRv.apply {
            adapter = payMemberRVAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            setHasFixedSize(TRUE)
        }

        payMemberRVAdapter.setPeopleItemClickListener(object :
            GroupPayMemberRVAdapter.PeopleItemClickListener {
            @SuppressLint("SetTextI18n")
            override fun onItemClick(
                peopleList: ArrayList<String>,
                memberIsChecked: MutableList<Pair<Int, Boolean>>
            ) {

                val checkedPeopleCount = memberIsChecked.count {
                    it.second
                }
                binding.groupPayCountTv.text = "$checkedPeopleCount"
            }
        })
    }

    private fun updateResultText() {
        val totalText = binding.groupPayTotalEt.text.toString()

        if (totalText.isNotEmpty() && totalText.toIntOrNull() != null) {
            val totalPay = totalText.toLong()
            val memberSize = binding.groupPayCountTv.text.toString().toLong()

            if (memberSize != 0L) {
                val result = totalPay / memberSize
                binding.groupPayResultTv.text = result.toString()
            } else {
                binding.groupPayResultTv.text = "0"
            }
        } else {
            // 빈 문자열
            binding.groupPayResultTv.text = "0"
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun onClickListener() {

        binding.apply {

            groupPayBackTv.setOnClickListener {
                dismiss()
            }
            groupPaySaveTv.setOnClickListener {

                val totalPay = binding.groupPayTotalEt.text.toString().toInt()
                pay(totalPay)

                val checkedMemberId = mutableListOf<Int>()

                for (i in memberIsChecked.indices) {
                    if (memberIsChecked[i].second) {
                        val userId = placeMember[i].userId
                        checkedMemberId.add(userId)
                    }
                }
                checkedMember(checkedMemberId)
                dismiss()
            }
        }
    }

    override fun onClick(p0: View?) {
        dismiss()
    }
}
