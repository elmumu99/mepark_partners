package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.common.User
import com.mrpark1.meparkpartner.data.model.user.*
import com.mrpark1.meparkpartner.data.repository.StartRepository
import com.mrpark1.meparkpartner.data.service.UserService
import retrofit2.Response
import javax.inject.Inject

class StartRepositoryImpl @Inject constructor(
    private val userService: UserService
) : StartRepository {

    override suspend fun addMyFRT(addMyFrtRequest: AddMyFrtRequest): Response<AddMyFrtResponse> =
        userService.addMyFRT(addMyFrtRequest)

    override suspend fun getMyAccessToken(getMyAccessTokenRequest: GetMyAccessTokenRequest): Response<GetMyAccessTokenResponse> =
        userService.getMyAccessToken(getMyAccessTokenRequest)


    override suspend fun getMyInfo(getMyInfoRequest: GetMyInfoRequest): Response<User> =
        userService.getMyInfo(getMyInfoRequest)

}