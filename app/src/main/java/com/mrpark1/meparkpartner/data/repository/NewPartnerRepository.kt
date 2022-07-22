package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.partner.ApplyNewPartnerRequest
import com.mrpark1.meparkpartner.data.model.partner.ApplyNewPartnerResponse
import retrofit2.Response

interface NewPartnerRepository {
    suspend fun applyNewPartner(applyNewPartnerRequest: ApplyNewPartnerRequest): Response<ApplyNewPartnerResponse>
}