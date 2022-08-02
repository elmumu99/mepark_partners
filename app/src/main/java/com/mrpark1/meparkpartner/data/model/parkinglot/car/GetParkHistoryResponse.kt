package com.mrpark1.meparkpartner.data.model.parkinglot.car

import com.mrpark1.meparkpartner.data.model.common.Car
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetParkHistoryResponse(
    val TotalExitCar: Int,
    val TotalReturnCar: Int,
    val ExitCars: List<Car>,
    val ReturnCars: List<Car>,
)