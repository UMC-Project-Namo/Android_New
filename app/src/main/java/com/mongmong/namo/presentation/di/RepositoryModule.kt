package com.mongmong.namo.presentation.di

import android.content.Context
import com.mongmong.namo.data.datasource.auth.RemoteAuthDataSource
import com.mongmong.namo.data.datasource.category.RemoteCategoryDataSource
import com.mongmong.namo.data.datasource.diary.RemoteActivityDataSource
import com.mongmong.namo.data.datasource.diary.RemoteDiaryDataSource
import com.mongmong.namo.data.datasource.friend.RemoteFriendDataSource
import com.mongmong.namo.data.datasource.profile.RemoteProfileDataSource
import com.mongmong.namo.data.datasource.s3.ImageDataSource
import com.mongmong.namo.data.datasource.schedule.RemoteScheduleDataSource
import com.mongmong.namo.data.datasource.terms.RemoteTermDataSource
import com.mongmong.namo.data.remote.DiaryApiService
import com.mongmong.namo.data.remote.NetworkChecker
import com.mongmong.namo.data.repositoriyImpl.ActivityRepositoryImpl
import com.mongmong.namo.data.repositoriyImpl.AuthRepositoryImpl
import com.mongmong.namo.data.repositoriyImpl.CategoryRepositoryImpl
import com.mongmong.namo.data.repositoriyImpl.DiaryRepositoryImpl
import com.mongmong.namo.data.repositoriyImpl.FriendRepositoryImpl
import com.mongmong.namo.data.repositoriyImpl.ImageRepositoryImpl
import com.mongmong.namo.data.repositoriyImpl.ProfileRepositoryImpl
import com.mongmong.namo.data.repositoriyImpl.ScheduleRepositoryImpl
import com.mongmong.namo.data.repositoriyImpl.TermRepositoryImpl
import com.mongmong.namo.domain.repositories.ActivityRepository
import com.mongmong.namo.domain.repositories.AuthRepository
import com.mongmong.namo.domain.repositories.CategoryRepository
import com.mongmong.namo.domain.repositories.DiaryRepository
import com.mongmong.namo.domain.repositories.FriendRepository
import com.mongmong.namo.domain.repositories.ImageRepository
import com.mongmong.namo.domain.repositories.ProfileRepository
import com.mongmong.namo.domain.repositories.ScheduleRepository
import com.mongmong.namo.domain.repositories.TermRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    /** 인증 */
    @Provides
    fun provideAuthRepository(
        remoteAuthDataSource: RemoteAuthDataSource,
    ): AuthRepository = AuthRepositoryImpl(remoteAuthDataSource)

    /** 약관 */
    @Provides
    fun provideTermRepository(
        remoteTermDataSource: RemoteTermDataSource
    ): TermRepository = TermRepositoryImpl(remoteTermDataSource)

    /** 일정 */
    @Provides
    fun provideScheduleRepository(
        remoteScheduleDataSource: RemoteScheduleDataSource,
        networkChecker: NetworkChecker
    ): ScheduleRepository = ScheduleRepositoryImpl(remoteScheduleDataSource, networkChecker)

    /** 기록 */
    @Provides
    fun provideDiaryRepository(
        remoteDiaryDataSource: RemoteDiaryDataSource,
        diaryService: DiaryApiService,
        networkChecker: NetworkChecker
    ): DiaryRepository = DiaryRepositoryImpl(
        remoteDiaryDataSource,
        diaryService,
        networkChecker
    )

    /** 활동 */
    @Provides
    fun provideActivityRepository(
        activityDataSource: RemoteActivityDataSource
    ): ActivityRepository = ActivityRepositoryImpl(activityDataSource)

    /** 친구 */
    @Provides
    fun provideFriendRepository(
        friendDataSource: RemoteFriendDataSource
    ): FriendRepository = FriendRepositoryImpl(friendDataSource)

    /** 카테고리 */
    @Provides
    fun provideCategoryRepository(
        remoteCategoryDataSource: RemoteCategoryDataSource,
        networkChecker: NetworkChecker
    ): CategoryRepository = CategoryRepositoryImpl(remoteCategoryDataSource, networkChecker)

    /** 프로필 */
    @Provides
    fun provideProfileRepository(
        remoteProfileDataSource: RemoteProfileDataSource
    ): ProfileRepository = ProfileRepositoryImpl(remoteProfileDataSource)

    /** s3 관련 */
    @Provides
    fun provideAwsS3Repository(
        awsS3DataSource: ImageDataSource,
        @ApplicationContext context: Context
    ): ImageRepository = ImageRepositoryImpl(
        awsS3DataSource, context
    )
}