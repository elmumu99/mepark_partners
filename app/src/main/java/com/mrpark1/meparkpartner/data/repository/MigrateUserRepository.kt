package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.user.GetUpdateEmailAuthCodeRequest
import com.mrpark1.meparkpartner.data.model.user.GetUpdateEmailAuthCodeResponse
import com.mrpark1.meparkpartner.data.model.user.UpdateUserEmailRequest
import com.mrpark1.meparkpartner.data.model.user.UpdateUserEmailResponse
import retrofit2.Response

interface MigrateUserRepository {
    suspend fun getUpdateEmailAuthCode(getUpdateEmailAuthCodeRequest: GetUpdateEmailAuthCodeRequest): Response<GetUpdateEmailAuthCodeResponse>
    suspend fun updateUserEmail(updateUserEmailRequest: UpdateUserEmailRequest): Response<UpdateUserEmailResponse>
}