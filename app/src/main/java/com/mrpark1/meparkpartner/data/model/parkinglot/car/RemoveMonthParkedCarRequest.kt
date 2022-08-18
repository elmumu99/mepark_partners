package com.mrpark1.meparkpartner.data.model.parkinglot.car

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoveMonthParkedCarRequest(
    val ParkingLN: String,
    val MonthCarID: String
)
