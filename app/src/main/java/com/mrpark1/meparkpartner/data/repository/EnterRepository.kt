package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.parkinglot.car.AddEntranceAndExitCarRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.AddEntranceAndExitCarResponse
import retrofit2.Response

interface EnterRepository {
    suspend fun addEntranceAndExitCar(addEntranceAndExitCarRequest: AddEntranceAndExitCarRequest): Response<AddEntranceAndExitCarResponse>
}