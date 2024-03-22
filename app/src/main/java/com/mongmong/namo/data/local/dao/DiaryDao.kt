package com.mongmong.namo.data.local.dao

import androidx.room.*
import com.mongmong.namo.R
import com.mongmong.namo.data.local.entity.diary.Diary
import com.mongmong.namo.data.local.entity.diary.DiaryEvent
import com.mongmong.namo.data.local.entity.home.Event

@Dao
interface DiaryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDiary(diary: Diary)

    @Update
    fun updateDiary(diary: Diary)

    @Query("DELETE FROM diary_table WHERE diaryId=:eventId")
    fun deleteDiary(eventId: Long)

    @Query("DELETE FROM diary_table")
    fun deleteAllDiaries()

    @Query("UPDATE calendar_event_table SET hasDiary= 1 WHERE eventId =:scheduleIdx")
    fun updateHasDiary(scheduleIdx: Long)

    @Query("UPDATE calendar_event_table SET hasDiary= 0 WHERE eventId =:scheduleIdx")
    fun deleteHasDiary(scheduleIdx: Long)

    @Query("SELECT * FROM diary_table WHERE diaryId=:scheduleId")
    fun getDiaryDaily(scheduleId: Long): Diary

    @Query(
        "SELECT * FROM calendar_event_table e JOIN diary_table d ON diaryId = eventId " +
                "WHERE strftime('%Y.%m', e.title, 'unixepoch') = :yearMonth AND d.state != ${R.string.event_current_deleted} " +
                "AND e.isMoim = 0 ORDER BY e.startDate DESC LIMIT :size OFFSET :page * :size"
    )
    fun getDiaryEventList(yearMonth: String, page: Int, size: Int): List<DiaryEvent>

    @Query("UPDATE diary_table SET isUpload=:isUpload, serverId=:serverId, state=:state WHERE diaryId=:localId")
    suspend fun updateDiaryAfterUpload(localId: Long, serverId: Long, isUpload: Int, state: String)

    @Query("SELECT * FROM diary_table WHERE isUpload = 0")
    fun getNotUploadedDiary(): List<Diary>

    @Query("SELECT * FROM calendar_event_table")
    fun getAllEvent(): List<Event>

}