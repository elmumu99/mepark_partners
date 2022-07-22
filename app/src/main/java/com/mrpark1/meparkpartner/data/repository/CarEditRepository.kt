package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.parkinglot.car.*
import retrofit2.Response

interface CarEditRepository {
    suspend fun updateEntranceAndExitCar(updateEntranceAndExitCarRequest: UpdateEntranceAndExitCarRequest): Response<UpdateEntranceAndExitCarResponse>
}