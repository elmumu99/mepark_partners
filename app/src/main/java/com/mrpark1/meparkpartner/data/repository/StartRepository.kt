package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.common.User
import com.mrpark1.meparkpartner.data.model.user.*
import retrofit2.Response

interface StartRepository {
    suspend fun addMyFRT(addMyFrtRequest: AddMyFrtRequest): Response<AddMyFrtResponse>
    suspend fun getMyAccessToken(getMyAccessTokenRequest: GetMyAccessTokenRequest): Response<GetMyAccessTokenResponse>
    suspend fun getMyInfo(getMyInfoRequest: GetMyInfoRequest): Response<User>
}