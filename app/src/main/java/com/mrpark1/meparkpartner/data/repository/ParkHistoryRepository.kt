package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.parkinglot.car.*
import retrofit2.Response

interface ParkHistoryRepository {
    suspend fun getParkHistory(getParkHistoryRequest: GetParkHistoryRequest): Response<GetParkHistoryResponse>
}