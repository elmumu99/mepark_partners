package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.managesale.GetMyParkingLotStatisticsRequest
import com.mrpark1.meparkpartner.data.model.managesale.GetMyParkingLotStatisticsResponse
import com.mrpark1.meparkpartner.data.model.managesale.GetMyPartnerStatisticsRequest
import com.mrpark1.meparkpartner.data.model.managesale.GetMyPartnerStatisticsResponse
import retrofit2.Response

interface ManageSaleRepository {

    suspend fun getMyPartnerStatistics(getMyPartnerStatisticsRequest: GetMyPartnerStatisticsRequest): Response<GetMyPartnerStatisticsResponse>

    suspend fun getMyParkingLotStatistics(getMyParkingLotStatisticsRequest: GetMyParkingLotStatisticsRequest): Response<GetMyParkingLotStatisticsResponse>
}