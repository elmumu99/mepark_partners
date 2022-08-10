package com.mrpark1.meparkpartner.data.model.parkinglot.car

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMonthParkedCarsRequest(
    val ParkingLN: String,
    val Mode: String  //모드 1이면 지금 계약중인 차량, 예정 차량 2면 계약 만료된 차량
)
