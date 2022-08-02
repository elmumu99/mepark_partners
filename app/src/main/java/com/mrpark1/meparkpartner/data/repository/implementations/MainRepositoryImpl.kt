package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.parkinglot.GetMyParkingLotsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsResponse
import com.mrpark1.meparkpartner.data.model.parkinglot.commute.UpdateCommutingRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.commute.UpdateCommutingResponse
import com.mrpark1.meparkpartner.data.model.partner.GetMyPartnerInfoRequest
import com.mrpark1.meparkpartner.data.model.partner.GetMyPartnerInfoResponse
import com.mrpark1.meparkpartner.data.repository.MainRepository
import com.mrpark1.meparkpartner.data.service.ParkingLotService
import retrofit2.Response
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val parkingLotService: ParkingLotService
) : MainRepository {

    override suspend fun getMyParkingLots(getMyParkingLotsRequest: GetMyParkingLotsRequest): Response<List<ParkingLot>> =
        parkingLotService.getMyParkingLots(getMyParkingLotsRequest)

    override suspend fun getParkedCars(getParkedCarsRequest: GetParkedCarsRequest): Response<GetParkedCarsResponse> =
        parkingLotService.getParkedCars(getParkedCarsRequest)

    override suspend fun updateCommuting(updateCommutingRequest: UpdateCommutingRequest): Response<UpdateCommutingResponse> =
        parkingLotService.updateCommuting(updateCommutingRequest)


}