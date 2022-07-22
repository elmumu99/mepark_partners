package com.mrpark1.meparkpartner.data.model.parkinglot.car

import com.mrpark1.meparkpartner.data.model.common.Car
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetParkedCarsResponse(
    val Cars: List<Car>,
    val NowDateTime: String
)