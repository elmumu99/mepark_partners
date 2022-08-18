package com.mrpark1.meparkpartner.data.model.parkinglot.car

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateMonthParkedCarRequest(
    val ParkingLN: String,
    val MonthCarID: String,
    val LP: String,
    val CarType: String,
    val Contact: String,
    val Memo: String,
    val StartDate: String,
    val EndDate: String,
    val Payment: String,
    val Profit: String,
)
