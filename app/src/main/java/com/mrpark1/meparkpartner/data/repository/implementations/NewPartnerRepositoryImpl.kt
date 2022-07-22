package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.partner.ApplyNewPartnerRequest
import com.mrpark1.meparkpartner.data.model.partner.ApplyNewPartnerResponse
import com.mrpark1.meparkpartner.data.repository.NewPartnerRepository
import com.mrpark1.meparkpartner.data.service.PartnerService
import retrofit2.Response
import javax.inject.Inject

class NewPartnerRepositoryImpl @Inject constructor(
    private val partnerService: PartnerService
) : NewPartnerRepository {

    override suspend fun applyNewPartner(applyNewPartnerRequest: ApplyNewPartnerRequest): Response<ApplyNewPartnerResponse> =
        partnerService.applyNewPartner(applyNewPartnerRequest)

}