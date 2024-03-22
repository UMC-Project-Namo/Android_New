package com.mongmong.namo.data.local.entity.home

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mongmong.namo.R
import java.io.Serializable

@Entity(tableName = "calendar_event_table")
data class Event(
    @PrimaryKey(autoGenerate = true)
    var eventId: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "startDate")
    var startLong: Long = 0,

    @ColumnInfo(name = "endDate")
    var endLong: Long = 0,

    @ColumnInfo(name = "dayInterval")
    var endDate: Int = 0,

    @ColumnInfo(name = "categoryId")
    var categoryIdx: Long = 0L,

    @ColumnInfo(name = "place")
    var placeName: String = "없음",

    @ColumnInfo(name = "placeX")
    var place: Double = 0.0,

    @ColumnInfo(name = "placeY")
    var placeY: Double = 0.0,

    @ColumnInfo(name = "order")
    var order: Int = 0,

    @ColumnInfo(name = "alarmList")
    var alarmList: List<Int>? = listOf(),

    @ColumnInfo(name = "isUpload")
    var isUpload: Int = 0,

    @ColumnInfo(name = "state")
    var state: String = R.string.event_current_default.toString(),

    @ColumnInfo(name = "serverId")
    var serverIdx: Long = 0L,

    @ColumnInfo(name = "categoryServerId")
    var categoryServerIdx: Long = 0L,

    @ColumnInfo(name = "hasDiary")
    var hasDiary: Int = 0,

    @ColumnInfo(name = "isMoim")
    var moimSchedule: Boolean = false

) : Serializable {
    fun eventToEventForUpload() : EventForUpload {
        return EventForUpload(
            name = this.title,
            startDate = this.startLong,
            endDate = this.endLong,
            interval = this.endDate,
            alarmDate = this.alarmList,
            x = this.place,
            y = this.placeY,
            locationName = this.placeName,
            categoryId = this.categoryServerIdx,
        )
    }
}


data class EventForUpload(
    var name: String = "",
    var startDate: Long = 0L,
    var endDate: Long = 0L,
    var interval: Int = 0,
    var alarmDate: List<Int>? = listOf(),
    var x: Double = 0.0,
    var y: Double = 0.0,
    var locationName: String = "없음",
    var categoryId: Long = 0L
)