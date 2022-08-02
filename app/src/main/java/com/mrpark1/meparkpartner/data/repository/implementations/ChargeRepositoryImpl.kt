package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.partner.GetMyPartnerInfoRequest
import com.mrpark1.meparkpartner.data.model.partner.GetMyPartnerInfoResponse
import com.mrpark1.meparkpartner.data.model.payment.GetChargingHistoryRequest
import com.mrpark1.meparkpartner.data.model.payment.GetChargingHistoryResponse
import com.mrpark1.meparkpartner.data.model.payment.GetPointHistoryRequest
import com.mrpark1.meparkpartner.data.model.payment.GetPointHistoryResponse
import com.mrpark1.meparkpartner.data.repository.ChargeRepository
import com.mrpark1.meparkpartner.data.service.PartnerService
import retrofit2.Response
import javax.inject.Inject

class ChargeRepositoryImpl @Inject constructor(
    private val partnerService: PartnerService
) : ChargeRepository{
    override suspend fun getChargingHistory(getChargeHistoryRequest: GetChargingHistoryRequest): Response<GetChargingHistoryResponse> =
        partnerService.getCharginHistory(getChargeHistoryRequest)

    override suspend fun getPointHistory(getPointHistoryRequest: GetPointHistoryRequest): Response<GetPointHistoryResponse> =
        partnerService.getPointHistory(getPointHistoryRequest)

    override suspend fun getMyPartnerInfo(getMyPartnerInfoRequest: GetMyPartnerInfoRequest): Response<GetMyPartnerInfoResponse> =
        partnerService.getMyPartnerInfo(getMyPartnerInfoRequest)

}