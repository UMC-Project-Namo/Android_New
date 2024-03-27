package com.mongmong.namo.presentation.ui.bottom.home.schedule

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongmong.namo.data.local.entity.home.Schedule
import com.mongmong.namo.domain.repositories.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {
    private val _schedule = MutableLiveData<Schedule>()
    val schedule: LiveData<Schedule> = _schedule

    private val _scheduleList = MutableLiveData<List<Schedule>>(emptyList())
    val scheduleList: LiveData<List<Schedule>?> = _scheduleList

    /** 선택한 날짜의 일정 조회 */
    fun getDailySchedules(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            Log.d("ScheduleViewModel", "getDailySchedules")
            _scheduleList.value = repository.getDailySchedules(startDate, endDate)
        }
    }

    /** 일정 추가 */
    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            Log.d("ScheduleViewModel", "addSchedule $schedule")
            repository.addSchedule(
                schedule = schedule
            )
        }
    }

    /** 일정 수정 */
    fun editSchedule(schedule: Schedule) {
        viewModelScope.launch {
            Log.d("ScheduleViewModel", "editSchedule $schedule")
            repository.editSchedule(
                schedule = schedule
            )
        }
    }

    /** 일정 삭제 */
    fun deleteSchedule(localId: Long, serverId: Long) {
        viewModelScope.launch {
            Log.d("ScheduleViewModel", "deleteSchedule $schedule")
            repository.deleteSchedule(
                localId = localId,
                serverId = serverId
            )
        }
    }
}