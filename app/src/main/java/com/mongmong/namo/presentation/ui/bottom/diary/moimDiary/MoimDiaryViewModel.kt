package com.mongmong.namo.presentation.ui.bottom.diary.moimDiary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongmong.namo.domain.model.MoimActivity
import com.mongmong.namo.domain.model.MoimDiaryResult
import com.mongmong.namo.domain.repositories.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoimDiaryViewModel @Inject constructor(
    private val repository: DiaryRepository
) : ViewModel() {
    private val _getMoimDiaryResult = MutableLiveData<MoimDiaryResult>()
    val getMoimDiaryResult : LiveData<MoimDiaryResult> = _getMoimDiaryResult

    private val _patchMemoResult = MutableLiveData<Boolean>()
    val patchMemoResult : LiveData<Boolean> = _patchMemoResult

    private val _patchActivitiesComplete = MutableLiveData<Boolean>()
    val patchActivitiesComplete : LiveData<Boolean> = _patchActivitiesComplete

    private val _deleteDiaryComplete = MutableLiveData<Boolean>()
    val deleteDiaryComplete : LiveData<Boolean> = _deleteDiaryComplete

    private val _memo = MutableLiveData<String>()

    /** 모임 기록 개별 조회 **/
    fun getMoimDiary(scheduleId: Long) {
        viewModelScope.launch {
            Log.d("MoimDiaryViewModel getMoimDiary", "$scheduleId")
            _getMoimDiaryResult.postValue(repository.getMoimDiary(scheduleId))
        }
    }


    /** 모임 메모 수정 **/
    fun patchMoimMemo(scheduleId: Long, content: String) {
        viewModelScope.launch {
            _patchMemoResult.postValue(repository.patchMoimMemo(scheduleId, content))
        }
    }

    fun patchMoimActivities(
        preActivities: List<MoimActivity>,
        memberIntList: List<Long>,
        scheduleId: Long,
        placeSchedule: List<MoimActivity>,
        deleteItems: List<Long>
    ) {
        viewModelScope.launch {
            placeSchedule.map { activity ->
                addOrEditMoimActivities(preActivities, activity, memberIntList, scheduleId)
            }
            deleteItems.map { activityId ->
               deleteMoimActivity(activityId)
            }
            // 모든 작업이 완료된 후에 상태 업데이트
            _patchActivitiesComplete.value = true
        }
    }

    private suspend fun addOrEditMoimActivities(
        preActivities: List<MoimActivity>,
        activity: MoimActivity,
        memberIntList: List<Long>,
        scheduleId: Long
    ) {
        val hasDiffer = preActivities.any { preActivity ->
            preActivity.place == activity.place &&
                    preActivity.pay == activity.pay &&
                    preActivity.members == activity.members &&
                    preActivity.imgs == activity.imgs
        }
        val members = activity.members.ifEmpty { memberIntList }
        if (!hasDiffer) {
            if (activity.moimActivityId == 0L) {
                addMoimActivity(
                    scheduleId,
                    activity.place,
                    activity.pay,
                    members,
                    activity.imgs
                )
            } else {
                editMoimActivity(
                    activity.moimActivityId,
                    activity.place,
                    activity.pay,
                    members,
                    activity.imgs
                )
            }

        }
    }

    /** 모임 기록 활동 추가 **/
    private suspend fun addMoimActivity(
        moimScheduleId: Long,
        place: String,
        money: Long,
        members: List<Long>?,
        images: List<String>?
    ) {
        Log.d("MoimActivity", "viewModel addMoimActivity")
        repository.addMoimActivity(moimScheduleId, place, money, members, images)
    }

    /** 모임 기록 활동 수정 **/
    private suspend fun editMoimActivity(
        moimScheduleId: Long,
        place: String,
        money: Long,
        members: List<Long>?,
        images: List<String>?
    ) {
        Log.d("MoimActivity", "viewModel editMoimActivity")
        repository.editMoimActivity(moimScheduleId, place, money, members, images)
    }

    /** 모임 기록 활동 삭제 **/
    private suspend fun deleteMoimActivity(activityId: Long) {
        Log.d("MoimActivity", "viewModel deleteMoimActivity")
        repository.deleteMoimActivity(activityId)
    }

    /** 모임 기록 삭제 (그룹에서) **/
    suspend fun deleteMoimDiary(diaryId: Long) {
        _deleteDiaryComplete.postValue(repository.deleteMoimDiary(diaryId))
    }



    fun setMemo(memo: String) { _memo.value = memo }
    fun getMemo() = _memo.value
}