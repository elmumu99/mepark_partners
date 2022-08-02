package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.managesale.GetMyParkingLotStatisticsRequest
import com.mrpark1.meparkpartner.data.model.managesale.GetMyParkingLotStatisticsResponse
import com.mrpark1.meparkpartner.data.model.managesale.GetMyPartnerStatisticsRequest
import com.mrpark1.meparkpartner.data.model.managesale.GetMyPartnerStatisticsResponse
import com.mrpark1.meparkpartner.data.repository.ManageSaleRepository
import com.mrpark1.meparkpartner.data.service.ManageSaleService
import retrofit2.Response
import javax.inject.Inject

class ManageSaleRepositoryImpl @Inject constructor(
    private val manageSaleService: ManageSaleService
) : ManageSaleRepository {

    override suspend fun getMyPartnerStatistics(getMyPartnerStatisticsRequest: GetMyPartnerStatisticsRequest)
    : Response<GetMyPartnerStatisticsResponse> =
        manageSaleService.getMyPartnerStatistics(getMyPartnerStatisticsRequest)

    override suspend fun getMyParkingLotStatistics(getMyParkingLotStatisticsRequest: GetMyParkingLotStatisticsRequest)
    : Response<GetMyParkingLotStatisticsResponse> =
        manageSaleService.getMyParkingLotStatistics(getMyParkingLotStatisticsRequest)

}