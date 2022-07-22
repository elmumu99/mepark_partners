package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.payment.GetChargingHistoryRequest
import com.mrpark1.meparkpartner.data.model.payment.GetChargingHistoryResponse
import com.mrpark1.meparkpartner.data.model.payment.GetPointHistoryRequest
import com.mrpark1.meparkpartner.data.model.payment.GetPointHistoryResponse
import retrofit2.Response

interface ChargeRepository {
    suspend fun getChargingHistory(getChargeHistoryRequest: GetChargingHistoryRequest): Response<GetChargingHistoryResponse>

    suspend fun getPointHistory(getPointHistoryRequest: GetPointHistoryRequest): Response<GetPointHistoryResponse>
}