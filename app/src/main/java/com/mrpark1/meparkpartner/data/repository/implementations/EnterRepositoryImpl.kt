package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.parkinglot.car.AddEntranceAndExitCarRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.AddEntranceAndExitCarResponse
import com.mrpark1.meparkpartner.data.repository.EnterRepository
import com.mrpark1.meparkpartner.data.service.ParkingLotService
import retrofit2.Response
import javax.inject.Inject

class EnterRepositoryImpl @Inject constructor(
    private val parkingLotService: ParkingLotService
) : EnterRepository {

    override suspend fun addEntranceAndExitCar(addEntranceAndExitCarRequest: AddEntranceAndExitCarRequest): Response<AddEntranceAndExitCarResponse> =
        parkingLotService.addEntranceAndExitCar(addEntranceAndExitCarRequest)

}