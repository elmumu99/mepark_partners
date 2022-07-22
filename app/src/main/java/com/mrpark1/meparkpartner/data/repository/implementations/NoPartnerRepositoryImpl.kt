package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.common.User
import com.mrpark1.meparkpartner.data.model.user.*
import com.mrpark1.meparkpartner.data.repository.NoPartnerRepository
import com.mrpark1.meparkpartner.data.service.UserService
import retrofit2.Response
import javax.inject.Inject

class NoPartnerRepositoryImpl @Inject constructor(
    private val userService: UserService
) : NoPartnerRepository {

    override suspend fun getMyInfo(getMyInfoRequest: GetMyInfoRequest): Response<User> =
        userService.getMyInfo(getMyInfoRequest)

    override suspend fun getMyPartnerInvitation(getMyPartnerInvitationRequest: GetMyPartnerInvitationRequest): Response<MutableList<GetMyPartnerInvitationResponse>> =
        userService.getMyPartnerInvitation(getMyPartnerInvitationRequest)

    override suspend fun applyMyPartner(applyMyPartnerRequest: ApplyMyPartnerRequest): Response<ApplyMyPartnerResponse> =
        userService.applyMyPartner(applyMyPartnerRequest)
}