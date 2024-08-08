package com.mongmong.namo.data.remote

import com.mongmong.namo.domain.model.AuthResponse
import retrofit2.http.POST

interface AuthApiService {
    // 로그아웃
    @POST("auths/logout")
    suspend fun postLogout(): AuthResponse

    /** 회원탈퇴 */
    // 카카오 회원탈퇴
    @POST("auths/kakao/delete")
    suspend fun postKakaoQuit(): AuthResponse

    // 네이버 회원탈퇴
    @POST("auths/naver/delete")
    suspend fun postNaverQuit(): AuthResponse
}