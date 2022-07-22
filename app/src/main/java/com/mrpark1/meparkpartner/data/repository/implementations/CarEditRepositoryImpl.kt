package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.parkinglot.car.UpdateEntranceAndExitCarRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.UpdateEntranceAndExitCarResponse
import com.mrpark1.meparkpartner.data.repository.CarEditRepository
import com.mrpark1.meparkpartner.data.service.ParkingLotService
import retrofit2.Response
import javax.inject.Inject

class CarEditRepositoryImpl @Inject constructor(
    private val parkingLotService: ParkingLotService
) : CarEditRepository {

    override suspend fun updateEntranceAndExitCar(updateEntranceAndExitCarRequest: UpdateEntranceAndExitCarRequest): Response<UpdateEntranceAndExitCarResponse> =
        parkingLotService.updateEntranceAndExitCar(updateEntranceAndExitCarRequest)

}