package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.parkinglot.GetMyParkingLotsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsResponse
import retrofit2.Response

interface ParkingLotSettingRepository {
    suspend fun getMyParkingLots(getMyParkingLotsRequest: GetMyParkingLotsRequest): Response<List<ParkingLot>>
}