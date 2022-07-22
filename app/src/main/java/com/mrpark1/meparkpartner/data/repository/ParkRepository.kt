package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsResponse
import com.mrpark1.meparkpartner.data.model.parkinglot.car.UpdateEntranceAndExitCarRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.UpdateEntranceAndExitCarResponse
import retrofit2.Response

interface ParkRepository {
    suspend fun getParkedCars(getParkedCarsRequest: GetParkedCarsRequest): Response<GetParkedCarsResponse>

    suspend fun updateEntranceAndExitCar(updateEntranceAndExitCarRequest: UpdateEntranceAndExitCarRequest): Response<UpdateEntranceAndExitCarResponse>
}