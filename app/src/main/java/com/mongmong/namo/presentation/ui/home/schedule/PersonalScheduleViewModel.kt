package com.mongmong.namo.presentation.ui.home.schedule

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.vectormap.LatLng
import com.mongmong.namo.domain.model.CategoryModel
import com.mongmong.namo.domain.model.Schedule
import com.mongmong.namo.data.dto.PatchMoimScheduleAlarmRequestBody
import com.mongmong.namo.data.dto.PatchMoimScheduleCategoryRequestBody
import com.mongmong.namo.domain.model.Location
import com.mongmong.namo.domain.model.SchedulePeriod
import com.mongmong.namo.domain.repositories.ScheduleRepository
import com.mongmong.namo.domain.usecases.category.FindCategoryUseCase
import com.mongmong.namo.domain.usecases.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class PersonalScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val findCategoryUseCase: FindCategoryUseCase
) : ViewModel() {
    private val _schedule = MutableLiveData<Schedule?>()
    val schedule: LiveData<Schedule?> = _schedule

    private val _scheduleList = MutableLiveData<List<Schedule>>(emptyList())
    val scheduleList: LiveData<List<Schedule>> = _scheduleList

    private val _isComplete = MutableLiveData<Boolean>()
    val isComplete: LiveData<Boolean> = _isComplete

    private val _category = MutableLiveData<CategoryModel>()
    var category: LiveData<CategoryModel> = _category

    private val _categoryList = MutableLiveData<List<CategoryModel>>(emptyList())
    val categoryList: LiveData<List<CategoryModel>> = _categoryList

    private val _prevClickedPicker = MutableLiveData<TextView?>()
    var prevClickedPicker: LiveData<TextView?> = _prevClickedPicker

    private val _monthDayList = MutableLiveData<List<DateTime>>()

    // 클릭한 날짜 처리
    private val _clickedDateTime = MutableLiveData<DateTime>()
    val clickedDateTime: LiveData<DateTime> = _clickedDateTime

    private var _dailyScheduleList: List<Schedule> = emptyList() // 하루 일정

    private val _isShow = MutableLiveData(false)
    var isShow: LiveData<Boolean> = _isShow

    private var _prevIndex = -1 // 클릭한 날짜의 index
    private var _nowIndex = 0 // 클릭한 날짜의 index

    private val _isDailyScheduleEmptyPair = MutableLiveData<Pair<Boolean, Boolean>>()
    var isDailyScheduleEmptyPair: LiveData<Pair<Boolean, Boolean>> = _isDailyScheduleEmptyPair

    // 다이얼로그 상단 휴지통 버튼 노출 여부
    private val _isShowTopDeleteBtn = MutableLiveData<Boolean>()
    var isShowTopDeleteBtn: LiveData<Boolean> = _isShowTopDeleteBtn

    init {
        getCategories() // 최초 카테고리 목록 조회
    }

    /** 월별 일정 리스트 조회 */
    fun getMonthSchedules() {
        viewModelScope.launch {
            // 범위로 일정 목록 조회
            _scheduleList.value = repository.getMonthSchedules(
                startDate = _monthDayList.value!!.first(), // 캘린더에 표시되는 첫번쨰 날짜
                endDate = _monthDayList.value!!.last() // 캘린더에 표시되는 마지막 날짜
            )
        }
    }

    /** 일정 생성 */
    fun addSchedule() {
        viewModelScope.launch {
            Log.d("ScheduleViewModel", "addSchedule ${_schedule.value}")
            _isComplete.value = (
                    repository.addSchedule(
                        schedule = _schedule.value!!
                    )
            )
        }
    }

    /** 일정 수정 */
    fun editSchedule() {
        viewModelScope.launch {
            Log.d("ScheduleViewModel", "editSchedule ${_schedule.value}")
            _isComplete.postValue(
                repository.editSchedule(
                    scheduleId = _schedule.value!!.scheduleId,
                    schedule = _schedule.value!!
                )
            )
        }
    }

    /** 일정 삭제 */
    fun deleteSchedule() {
        viewModelScope.launch {
            Log.d("ScheduleViewModel", "deleteSchedule $schedule")
            _isComplete.postValue(
                repository.deleteSchedule(
                    scheduleId = _schedule.value!!.scheduleId,
                )
            )
        }
    }

    // 모임
    /** 모임 일정 카테고리 수정 */
    fun editMoimScheduleCategory() {
        viewModelScope.launch {
            _isComplete.postValue(
                repository.editMoimScheduleCategory(
                    PatchMoimScheduleCategoryRequestBody(
                        _schedule.value!!.scheduleId,
                        _schedule.value!!.categoryInfo.categoryId
                    )
                )
            )
        }
    }

    /** 모임 일정 알림 수정 */
    fun editMoimScheduleAlert(scheduleId: Long, alertList: List<Int>) {
        viewModelScope.launch {
            repository.editMoimScheduleAlert(
                PatchMoimScheduleAlarmRequestBody(
                    scheduleId,
                    alertList
                )
            )
        }
    }

    /** 카테고리 조회 */
    fun getCategories() {
        viewModelScope.launch {
            _categoryList.value = getCategoriesUseCase.invoke()
        }
    }

    /** 카테고리 id로 카테고리 조회 */
    fun findCategoryById() {
        viewModelScope.launch {
            // 카테고리 찾기
            _category.value = schedule.value?.let { schedule ->
                if (schedule.scheduleId == 0L && schedule.categoryInfo.categoryId == 0L) { // 새 일정인 경우
//                    Log.d(
//                        "ScheduleViewModel",
//                        "findCategoryById() - categoryList:  ${_categoryList.value}"
//                    )
                    _categoryList.value?.first()
                } else {
                    findCategoryUseCase.invoke(schedule.categoryInfo.categoryId)
                }
            }
            setCategory()
//            Log.e("ScheduleViewModel", "findCategoryById() - category: ${_category.value}")
        }
    }

    /** 일정 정보 세팅 */
    fun setSchedule(schedule: Schedule?) {
        _schedule.value = schedule
        Log.d("ScheduleViewModel", "schedule: ${_schedule.value}")
        if (schedule?.locationInfo?.locationName!!.isBlank()) {
            _schedule.value?.locationInfo?.locationName = "없음"
        }
    }

    fun setCategory() {
        Log.d("ScheduleViewModel", "setCategory()")
        // 일정에 카테고리 정보 넣기
        _schedule.value = _schedule.value?.copy(
            categoryInfo = _category.value!!.convertCategoryToScheduleCategory()
        )
    }

    private fun setDailySchedule() {
        // 선택 날짜에 해당되는 일정 필터링
        _dailyScheduleList = _scheduleList.value!!.filter { schedule ->
            schedule.period.startDate <= getClickedDatePeriod().endDate &&
                    schedule.period.endDate >= getClickedDatePeriod().startDate
        }
        _isDailyScheduleEmptyPair.value = Pair(
            isDailyScheduleEmpty(false), // 개인 일정
            isDailyScheduleEmpty(true) // 모임 일정
        )
    }

    // 캘린더의 날짜 클릭
    fun onClickCalendarDate(index: Int) {
        _nowIndex = index // 클릭한 번호 저장
        _clickedDateTime.value = getClickedDate() // 클릭한 날짜 저장
        setDailySchedule()
    }

    // 클릭한 날짜의 시작, 종료 시간
    private fun getClickedDatePeriod(): SchedulePeriod {
        return SchedulePeriod(
            getClickedDate().toLocalDateTime(), // 날짜 시작일
            getClickedDate() // 날짜 종료일
                .plusDays(1)
                .withTimeAtStartOfDay()
                .minusMillis(1)
                .toLocalDateTime()
        )
    }

    fun updateIsShow() {
        _isShow.value = !_isShow.value!!
        _prevIndex = _nowIndex
    }

    // 일정 상세 바텀 시트 닫기 - 동일한 날짜를 다시 클릭했을 경우
    fun isCloseScheduleDetailBottomSheet() = _isShow.value == true && (_prevIndex == _nowIndex)

    // 캘린더에 들어갈 한달 날짜 리스트
    fun setMonthDayList(monthDayList: List<DateTime>) {
        _monthDayList.value = monthDayList
    }

    // 시간 변경
    fun updateTime(startDateTime: LocalDateTime?, endDateTime: LocalDateTime?) {
        _schedule.value = _schedule.value?.copy(
            period = SchedulePeriod(
                startDate = startDateTime
                    ?: _schedule.value!!.period.startDate,
                endDate = endDateTime
                    ?: _schedule.value!!.period.endDate
            ),
        )
    }

    // 장소 선택
    fun updatePlace(placeName: String, x: Double, y: Double) {
        _schedule.value = _schedule.value?.copy(
            locationInfo = Location(
                locationName = placeName,
                longitude = y,
                latitude = x
            )
        )
    }

    fun updateCategory(category: CategoryModel) {
        _category.value = category
        setCategory()
    }

    fun updatePrevClickedPicker(clicked: TextView?) {
        _prevClickedPicker.value = clicked
    }

    fun isMoimSchedule() = schedule.value!!.isMeetingSchedule

    fun isCreateMode() = (schedule.value!!.scheduleId == 0L)

    private fun isDailyScheduleEmpty(isMoim: Boolean): Boolean {
        Log.d("ScheduleViewModel", "isDailyScheduleEmpty($isMoim): ${getDailySchedules(isMoim)}")
        return getDailySchedules(isMoim).isEmpty()
    }

    // 선택한 날짜
    fun getClickedDate() = _monthDayList.value!![_nowIndex]

    fun getDailySchedules(isMoim: Boolean): ArrayList<Schedule> {
        return _dailyScheduleList.filter { schedule ->
            schedule.isMeetingSchedule == isMoim
        } as ArrayList<Schedule>
    }

    fun getDateTime(): SchedulePeriod? {
        if (_schedule.value != null) {
            return schedule.value!!.period
        }
        return null
    }

    fun getPlace(): Pair<String, LatLng>? {
        if (_schedule.value != null) {
            if (_schedule.value!!.locationInfo.longitude == 0.0 && _schedule.value!!.locationInfo.latitude == 0.0) return null
            return Pair(
                schedule.value!!.locationInfo.locationName,
                LatLng.from(
                    schedule.value!!.locationInfo.longitude,
                    schedule.value!!.locationInfo.latitude
                )
            )
        }
        return null
    }

    fun setDeleteBtnVisibility(isVisible: Boolean) {
        _isShowTopDeleteBtn.value = isVisible
    }

    fun isInvalidDate(): Boolean {
        // 시작일 > 종료일
        return schedule.value!!.period.startDate > schedule.value!!.period.endDate
    }
}