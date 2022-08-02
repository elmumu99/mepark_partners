package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.parkinglot.GetMyParkingLotsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetParkedCarsResponse
import com.mrpark1.meparkpartner.data.model.parkinglot.commute.UpdateCommutingRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.commute.UpdateCommutingResponse
import com.mrpark1.meparkpartner.data.model.partner.GetMyPartnerInfoRequest
import com.mrpark1.meparkpartner.data.model.partner.GetMyPartnerInfoResponse
import retrofit2.Response

interface MainRepository {
    suspend fun getMyParkingLots(getMyParkingLotsRequest: GetMyParkingLotsRequest): Response<List<ParkingLot>>

    suspend fun getParkedCars(getParkedCarsRequest: GetParkedCarsRequest): Response<GetParkedCarsResponse>

    suspend fun updateCommuting(updateCommutingRequest: UpdateCommutingRequest): Response<UpdateCommutingResponse>


}