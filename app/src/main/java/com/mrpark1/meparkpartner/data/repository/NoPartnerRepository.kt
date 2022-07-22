package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.common.User
import com.mrpark1.meparkpartner.data.model.user.*
import retrofit2.Response

interface NoPartnerRepository {
    suspend fun getMyInfo(getMyInfoRequest: GetMyInfoRequest): Response<User>
    suspend fun getMyPartnerInvitation(getMyPartnerInvitationRequest: GetMyPartnerInvitationRequest): Response<MutableList<GetMyPartnerInvitationResponse>>
    suspend fun applyMyPartner(applyMyPartnerRequest: ApplyMyPartnerRequest): Response<ApplyMyPartnerResponse>
}