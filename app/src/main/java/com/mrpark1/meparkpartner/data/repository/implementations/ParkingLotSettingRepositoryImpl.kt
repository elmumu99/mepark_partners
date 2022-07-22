package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.parkinglot.GetMyParkingLotsRequest
import com.mrpark1.meparkpartner.data.repository.ParkingLotSettingRepository
import com.mrpark1.meparkpartner.data.service.ParkingLotService
import retrofit2.Response
import javax.inject.Inject

class ParkingLotSettingRepositoryImpl @Inject constructor(
    private val parkingLotService: ParkingLotService
) : ParkingLotSettingRepository {

    override suspend fun getMyParkingLots(getMyParkingLotsRequest: GetMyParkingLotsRequest): Response<List<ParkingLot>> =
        parkingLotService.getMyParkingLots(getMyParkingLotsRequest)

}