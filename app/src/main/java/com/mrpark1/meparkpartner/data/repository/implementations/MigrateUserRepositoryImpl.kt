package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.user.GetUpdateEmailAuthCodeRequest
import com.mrpark1.meparkpartner.data.model.user.GetUpdateEmailAuthCodeResponse
import com.mrpark1.meparkpartner.data.model.user.UpdateUserEmailRequest
import com.mrpark1.meparkpartner.data.model.user.UpdateUserEmailResponse
import com.mrpark1.meparkpartner.data.repository.MigrateUserRepository
import com.mrpark1.meparkpartner.data.service.UserService
import retrofit2.Response
import javax.inject.Inject

class MigrateUserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : MigrateUserRepository {

    override suspend fun getUpdateEmailAuthCode(getUpdateEmailAuthCodeRequest: GetUpdateEmailAuthCodeRequest): Response<GetUpdateEmailAuthCodeResponse> =
        userService.getUpdateEmailAuthCode(getUpdateEmailAuthCodeRequest)

    override suspend fun updateUserEmail(updateUserEmailRequest: UpdateUserEmailRequest): Response<UpdateUserEmailResponse> =
        userService.updateUserEmail(updateUserEmailRequest)

}