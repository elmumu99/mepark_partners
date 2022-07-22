package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.parkinglot.car.*
import com.mrpark1.meparkpartner.data.repository.ParkHistoryRepository
import com.mrpark1.meparkpartner.data.repository.ParkRepository
import com.mrpark1.meparkpartner.data.service.ParkingLotService
import retrofit2.Response
import javax.inject.Inject

class ParkHistoryRepositoryImpl @Inject constructor(
    private val parkingLotService: ParkingLotService
) : ParkHistoryRepository {

    override suspend fun getParkHistory(getParkHistoryRequest: GetParkHistoryRequest): Response<GetParkHistoryResponse> =
        parkingLotService.getParkHistory(getParkHistoryRequest)

}