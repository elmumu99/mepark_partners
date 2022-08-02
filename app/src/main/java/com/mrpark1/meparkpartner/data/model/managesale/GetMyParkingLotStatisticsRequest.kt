package com.mrpark1.meparkpartner.data.model.managesale

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMyParkingLotStatisticsRequest(
    val ParkingLN: String,
    val StartDate: String,
    val EndDate: String
)
