package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsResponse
import com.mrpark1.meparkpartner.data.model.parkinglot.car.UpdateEntranceAndExitCarRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.UpdateEntranceAndExitCarResponse
import com.mrpark1.meparkpartner.data.repository.ParkRepository
import com.mrpark1.meparkpartner.data.service.ParkingLotService
import retrofit2.Response
import javax.inject.Inject

class ParkRepositoryImpl @Inject constructor(
    private val parkingLotService: ParkingLotService
) : ParkRepository {
    override suspend fun getParkedCars(getParkedCarsRequest: GetParkedCarsRequest): Response<GetParkedCarsResponse> =
        parkingLotService.getParkedCars(getParkedCarsRequest)

    override suspend fun updateEntranceAndExitCar(updateEntranceAndExitCarRequest: UpdateEntranceAndExitCarRequest): Response<UpdateEntranceAndExitCarResponse> =
        parkingLotService.updateEntranceAndExitCar(updateEntranceAndExitCarRequest)

}