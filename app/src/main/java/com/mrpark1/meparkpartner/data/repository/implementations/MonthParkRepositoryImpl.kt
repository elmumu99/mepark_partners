package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.parkinglot.car.*
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

    override suspend fun updateMonthParkedCar(updateMonthParkedCarRequest: UpdateMonthParkedCarRequest): Response<UpdateMonthParkedCarResponse> =
        parkingLotService.updateMonthParkedCar(updateMonthParkedCarRequest)

    override suspend fun removeMonthParkedCar(removeMonthParkedCarRequest: RemoveMonthParkedCarRequest): Response<RemoveMonthParkedCarResponse> =
        parkingLotService.removeMonthParkedCar(removeMonthParkedCarRequest)
}