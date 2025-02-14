package com.mongmong.namo.presentation.ui.community.moim.diary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mongmong.namo.databinding.ItemMoimDiaryActivityParticipantsBinding
import com.mongmong.namo.domain.model.PaymentParticipant

class ActivityPaymentsRVAdapter(
    private var participants: List<PaymentParticipant>,
    private val onCheckedChanged: () -> Unit,
    private val hasDiary: Boolean,
    private val isEdit: Boolean
) : RecyclerView.Adapter<ActivityPaymentsRVAdapter.ViewHolder>() {

    private val updatedParticipants = participants.toMutableList()

    // 새로운 리스트로 참가자 데이터를 업데이트하는 메서드
    fun updateParticipants(participants: List<PaymentParticipant>) {
        updatedParticipants.clear()
        updatedParticipants.addAll(participants)
        notifyDataSetChanged()  // 어댑터 갱신
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMoimDiaryActivityParticipantsBinding = ItemMoimDiaryActivityParticipantsBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val participant = updatedParticipants[position]
        holder.bind(participant)
    }

    override fun getItemCount(): Int = updatedParticipants.size

    inner class ViewHolder(val binding: ItemMoimDiaryActivityParticipantsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: PaymentParticipant) {
            binding.hasDiary = hasDiary
            binding.isEdit = isEdit

            binding.itemActivityParticipantsNicknameTv.text = participant.nickname
            binding.itemActivityParticipantsCheckbox.isChecked = participant.isPayer

            binding.itemActivityParticipantsCheckbox.setOnCheckedChangeListener { _, isChecked ->
                updatedParticipants[bindingAdapterPosition].isPayer = isChecked
                onCheckedChanged()
            }
        }
    }

    fun getSelectedParticipantsCount(): Int {
        return updatedParticipants.count { it.isPayer }
    }

    fun getUpdatedParticipants(): List<PaymentParticipant> {
        return updatedParticipants.toList() // 업데이트된 참가자 리스트 반환
    }
}


