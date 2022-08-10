package com.mrpark1.meparkpartner.data.model.parkinglot.car

import com.mrpark1.meparkpartner.data.model.common.MonthParkedCar
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMonthParkedCarsResponse(
    val TotalCar: Int,
    val CarResult: List<MonthParkedCar>
)
