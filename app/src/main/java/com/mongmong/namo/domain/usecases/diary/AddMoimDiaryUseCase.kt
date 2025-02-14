package com.mongmong.namo.domain.usecases.diary

import android.net.Uri
import com.mongmong.namo.domain.model.BaseResponse
import com.mongmong.namo.domain.model.DiaryDetail
import com.mongmong.namo.domain.repositories.DiaryRepository
import com.mongmong.namo.domain.usecases.image.UploadImageToS3UseCase
import javax.inject.Inject

class AddMoimDiaryUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val uploadImageToS3UseCase: UploadImageToS3UseCase
) {
    suspend fun execute(
        diary: DiaryDetail,
        scheduleId: Long
    ): BaseResponse {
        val newImageUrls = uploadImageToS3UseCase.execute(PREFIX, (diary.diaryImages).map { Uri.parse(it.imageUrl) })
        return diaryRepository.addDiary(
            content = diary.content,
            enjoyRating = diary.enjoyRating,
            images = newImageUrls,
            scheduleId = scheduleId
        )
    }

    companion object {
        const val PREFIX = "diary"
    }
}