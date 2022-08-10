package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.parkinglot.car.AddMonthParkedCarRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.AddMonthParkedCarResponse
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetMonthParkedCarsRequest
import com.mrpark1.meparkpartner.data.model.parkinglot.car.GetMonthParkedCarsResponse
import retrofit2.Response

interface MonthParkRepository {
    suspend fun addMonthParkedCar(addMonthParkedCarRequest: AddMonthParkedCarRequest): Response<AddMonthParkedCarResponse>

    suspend fun getMonthParkedCars(getMonthParkedCarsRequest: GetMonthParkedCarsRequest): Response<GetMonthParkedCarsResponse>
}