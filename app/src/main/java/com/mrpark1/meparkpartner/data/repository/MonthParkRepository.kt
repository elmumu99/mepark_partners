package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.parkinglot.car.*
import retrofit2.Response

interface MonthParkRepository {
    suspend fun addMonthParkedCar(addMonthParkedCarRequest: AddMonthParkedCarRequest): Response<AddMonthParkedCarResponse>

    suspend fun getMonthParkedCars(getMonthParkedCarsRequest: GetMonthParkedCarsRequest): Response<GetMonthParkedCarsResponse>

    suspend fun updateMonthParkedCar(updateMonthParkedCarRequest: UpdateMonthParkedCarRequest): Response<UpdateMonthParkedCarResponse>

    suspend fun removeMonthParkedCar(removeMonthParkedCarRequest: RemoveMonthParkedCarRequest): Response<RemoveMonthParkedCarResponse>
}