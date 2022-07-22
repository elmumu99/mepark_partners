package com.mrpark1.meparkpartner.data.model.parkinglot.car

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetParkedCarsRequest(
    val ParkingLN: String
)