package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.partner.ApplyNewPartnerRequest
import com.mrpark1.meparkpartner.data.model.partner.ApplyNewPartnerResponse
import com.mrpark1.meparkpartner.data.model.partner.CheckAccountRequest
import com.mrpark1.meparkpartner.data.model.partner.CheckAccountResponse
import retrofit2.Response

interface NewPartnerRepository {
    suspend fun applyNewPartner(applyNewPartnerRequest: ApplyNewPartnerRequest): Response<ApplyNewPartnerResponse>

    suspend fun checkAccount(checkAccountRequest: CheckAccountRequest): Response<CheckAccountResponse>
}