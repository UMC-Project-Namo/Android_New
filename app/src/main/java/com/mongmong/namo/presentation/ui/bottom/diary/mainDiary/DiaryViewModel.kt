package com.mongmong.namo.presentation.ui.bottom.diary.mainDiary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.mongmong.namo.data.local.entity.diary.Diary
import com.mongmong.namo.data.local.entity.diary.DiarySchedule
import com.mongmong.namo.data.local.entity.home.Schedule
import com.mongmong.namo.domain.repositories.DiaryRepository
import com.mongmong.namo.presentation.config.RoomState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import org.joda.time.DateTime
import java.text.SimpleDateFormat

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: DiaryRepository
) : ViewModel() {
    private val _diary = MutableLiveData<Diary>()
    val diary: LiveData<Diary> = _diary

    private val _imgList = MutableLiveData<List<String>>(emptyList())
    val imgList: LiveData<List<String>> = _imgList

    private val _currentDate = MutableLiveData<String>(DateTime().toString("yyyy.MM"))
    val currentDate : LiveData<String> = _currentDate

    private val _isMoim = MutableLiveData<Int>(0)
    val isMoim : LiveData<Int> = _isMoim

    /** 개인 기록 리스트 조회 **/
    fun getPersonalPaging(date: String): Flow<PagingData<DiarySchedule>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { repository.getPersonalDiaryPagingSource(date) }
        ).flow.cachedIn(viewModelScope).map { pagingData -> pagingData.insertHeaderLogic() }
    }

    /** 모임 기록 리스트 조회 **/
    fun getMoimPaging(date: String): Flow<PagingData<DiarySchedule>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { repository.getMoimDiaryPagingSource(date) }
        ).flow.cachedIn(viewModelScope).map { pagingData -> pagingData.insertHeaderLogic() }
    }

    // PagingData에 날짜 구분선 헤더 추가
    private fun PagingData<DiarySchedule>.insertHeaderLogic(): PagingData<DiarySchedule> {
        return this.insertSeparators { before, after ->
            Log.d("insertHeaderLogic", "${before?.startDate?.convertDate()}, ${after?.startDate?.convertDate()}" )
            if (after == null) { return@insertSeparators null }
            if (before == null || before.startDate.convertDate() != after.startDate.convertDate()) {
                // 첫 아이템, 날짜가 변경될 때 헤더 아이템 추가
                after.copy(startDate = after.startDate * 1000, isHeader = true)
            } else {
                // 같은 날짜 내의 아이템 처리
                null
            }
        }
    }
    private fun Long.convertDate() : String {
        return SimpleDateFormat("yyyy.MM.dd").format(this * 1000)
    }
    /** 개인 기록 개별 조회 **/
    fun getExistingDiary(diaryId: Long) {
        viewModelScope.launch {
            Log.d("DiaryViewModel getDiary", "$diaryId")
            _diary.postValue(repository.getDiary(diaryId))
        }
    }
    /** 개인 기록 추가시 데이터 초기화 **/
    fun setNewDiary(schedule: Schedule, content: String) {
        _diary.value = Diary(
            diaryId = schedule.scheduleId,
            scheduleServerId = schedule.serverId,
            content = content,
            images = _imgList.value,
            state = RoomState.ADDED.state
        )
    }

    /** 개인 기록 추가 **/
    fun addDiary(images: List<File>?) {
        viewModelScope.launch {
            Log.d("DiaryViewModel addDiary", "$diary")
            _diary.value?.let {
                repository.addDiary(
                    diary = it,
                    images = images
                )
            }
        }
    }
    /** 개인 기록 수정 **/
    fun editDiary(content: String, images: List<File>?) {
        viewModelScope.launch {
            _diary.value?.let {
                it.content = content
                it.state = RoomState.EDITED.state
            }
            Log.d("DiaryViewModel editDiary", "${_diary.value}")
            _diary.value?.let {
                repository.editDiary(
                    diary = it,
                    images = images
                )
            }
        }
    }
    /** 개인 기록 삭제 **/
    fun deleteDiary(localId: Long, scheduleServerId: Long) {
        viewModelScope.launch {
            repository.deleteDiary(localId, scheduleServerId)
        }
    }

    fun getImgList() = _imgList.value
    fun updateImgList(newImgList: List<String>) {
        _imgList.value = newImgList
        _diary.value?.images = _imgList.value
    }

    /** 선택 날짜 **/
    fun getCurrentDate(): String = _currentDate.value ?: DateTime().toString("yyyy.MM")
    fun setCurrentDate(yearMonth: String) { _currentDate.value = yearMonth }
    /** 개인/그룹 여부 토글  **/
    fun getIsGroup(): Int = _isMoim.value ?: 0
    fun setIsGroup(isGroup: Boolean) { _isMoim.value = if(isGroup) IS_GROUP else IS_NOT_GROUP }
    companion object {
        const val IS_GROUP = 1
        const val IS_NOT_GROUP = 0
        const val PAGE_SIZE = 5
    }
}