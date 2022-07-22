package com.mrpark1.meparkpartner.data.service

import com.mrpark1.meparkpartner.data.model.common.User
import com.mrpark1.meparkpartner.data.model.user.GetUpdateEmailAuthCodeRequest
import com.mrpark1.meparkpartner.data.model.user.GetUpdateEmailAuthCodeResponse
import com.mrpark1.meparkpartner.data.model.user.UpdateUserEmailRequest
import com.mrpark1.meparkpartner.data.model.user.UpdateUserEmailResponse
import com.mrpark1.meparkpartner.data.model.user.AddUserRequest
import com.mrpark1.meparkpartner.data.model.user.AddUserResponse
import com.mrpark1.meparkpartner.data.model.user.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    //유저와 관련된 API interface를 모아둡니다.

    @POST("/AddMyFRT")
    suspend fun addMyFRT(@Body request: AddMyFrtRequest): Response<AddMyFrtResponse>

    @POST("/GetMyAccessToken")
    suspend fun getMyAccessToken(@Body request: GetMyAccessTokenRequest): Response<GetMyAccessTokenResponse>

    @POST("/GetMyInfo")
    suspend fun getMyInfo(@Body request: GetMyInfoRequest): Response<User>

    @POST("/AddUser")
    suspend fun addUser(@Body request: AddUserRequest): Response<AddUserResponse>

    @POST("/GetUpdateEmailAuthCode")
    suspend fun getUpdateEmailAuthCode(@Body request: GetUpdateEmailAuthCodeRequest): Response<GetUpdateEmailAuthCodeResponse>

    @POST("/UpdateUserEmail")
    suspend fun updateUserEmail(@Body request: UpdateUserEmailRequest): Response<UpdateUserEmailResponse>

    @POST("/GetMyPartnerInvitation")
    suspend fun getMyPartnerInvitation(@Body request: GetMyPartnerInvitationRequest): Response<MutableList<GetMyPartnerInvitationResponse>>

    @POST("/ApplyMyPartner")
    suspend fun applyMyPartner(@Body request: ApplyMyPartnerRequest): Response<ApplyMyPartnerResponse>
}