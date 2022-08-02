package com.mrpark1.meparkpartner.data.service

import com.mrpark1.meparkpartner.data.model.managesale.GetMyParkingLotStatisticsRequest
import com.mrpark1.meparkpartner.data.model.managesale.GetMyParkingLotStatisticsResponse
import com.mrpark1.meparkpartner.data.model.managesale.GetMyPartnerStatisticsRequest
import com.mrpark1.meparkpartner.data.model.managesale.GetMyPartnerStatisticsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ManageSaleService {

    @POST("/GetMyPartnerStatistics")
    suspend fun getMyPartnerStatistics(@Body request: GetMyPartnerStatisticsRequest): Response<GetMyPartnerStatisticsResponse>

    @POST("/GetMyParkingLotStatistics")
    suspend fun getMyParkingLotStatistics(@Body request: GetMyParkingLotStatisticsRequest): Response<GetMyParkingLotStatisticsResponse>
}