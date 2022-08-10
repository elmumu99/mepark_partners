package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.parkinglot.car.AddMonthParkedCarRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.AddMonthParkedCarResponse
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetMonthParkedCarsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetMonthParkedCarsResponse
import com.mrpark1.meparkpartner.data.repository.MonthParkRepository
import com.mrpark1.meparkpartner.data.service.ParkingLotService
import retrofit2.Response
import javax.inject.Inject

class MonthParkRepositoryImpl @Inject constructor(
    private val parkingLotService: ParkingLotService): MonthParkRepository{
    override suspend fun addMonthParkedCar(addMonthParkedCarRequest: AddMonthParkedCarRequest): Response<AddMonthParkedCarResponse> =
        parkingLotService.addMonthParkedCar(addMonthParkedCarRequest)

    override suspend fun getMonthParkedCars(getMonthParkedCarsRequest: GetMonthParkedCarsRequest): Response<GetMonthParkedCarsResponse> =
        parkingLotService.getMonthParkedCars(getMonthParkedCarsRequest)
}