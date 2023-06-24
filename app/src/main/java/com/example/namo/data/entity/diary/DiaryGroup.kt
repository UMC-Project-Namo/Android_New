package com.example.namo.data.entity.diary

import android.os.Parcel
import android.os.Parcelable
import androidx.room.PrimaryKey

/** dummy **/
data class DiaryGroupEvent(
    @PrimaryKey(autoGenerate = true)
    val place: String? = "",
    val pay: Int = 0,
    val imgs: List<String>?
) :java.io.Serializable


data class GroupDiaryMember(
    val memberName: String = ""
)
