package com.mongmong.namo.domain.model

import com.mongmong.namo.presentation.config.BaseResponse
import com.google.gson.annotations.SerializedName


data class DiaryResponse(
    val result: String
) : BaseResponse() // 기본 string


/** 기록 추가 **/
data class DiaryAddResponse(
    val result: GetScheduleIdx
) : BaseResponse()

data class GetScheduleIdx(
    val scheduleId: Long
)

/** 기록 월 별 조회 **/
data class DiaryGetAllResponse(
    val result: List<DiaryGetAllResult>
) : BaseResponse()

data class DiaryGetAllResult(
    val scheduleId: Long,
    val contents: String?,
    val urls: List<String>,
)

data class DiarySchedule(
    var scheduleId: Long = 0L,
    var title: String = "",
    var startDate: Long = 0,
    var categoryId: Long = 0L,
    var place: String = "없음",
    var content: String?,
    var images: List<String>? = null,
    var serverId: Long = 0L, // eventServerId
    var categoryServerId : Long = 0L,
    var isHeader: Boolean = false
)

/** 모임 기록 개별 조회 **/
data class GetMoimDiaryResponse(
    val result: MoimDiaryResult
) : BaseResponse()

data class MoimDiaryResult(
    val name: String,
    val startDate: Long,
    val locationName: String,
    val users: List<GroupUser>,
    @SerializedName("locationDtos") val moimActivities: List<MoimActivity>
)

data class GroupUser(
    val userId: Long,
    val userName: String
) : java.io.Serializable

data class MoimActivity(
    @SerializedName("moimMemoLocationId") val moimActivityId: Long = 0L,
    @SerializedName("name") var place: String = "",
    @SerializedName("money") var pay: Long = 0L,
    @SerializedName("participants") var members: List<Long>,
    @SerializedName("urls") var imgs: List<String>?
) : java.io.Serializable


/** 모임 기록 월 별 조회 **/
data class DiaryGetMonthResponse(
    val result: GroupResult
) : BaseResponse()

data class GroupResult(
    val content: List<MoimDiary>,
    val currentPage: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
)

data class MoimDiary(
    @SerializedName("scheduleId") val scheduleId: Long,
    @SerializedName("name") val title: String,
    @SerializedName("startDate") var startDate: Long,
    @SerializedName("contents") val content: String?,
    @SerializedName("urls") val imgUrl: List<String>,
    @SerializedName("categoryId") val categoryId: Long,
    @SerializedName("color") val categoryColor: Long,
    @SerializedName("placeName") val placeName: String
) : java.io.Serializable


