package com.mrpark1.meparkpartner.data.model.parkinglot.visitplace

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddMyVisitPlacesRequest(
    val ParkingLN: String,
    val PlaceName: String,
    val FreeTime: String,
    val DefaultTime: String,
    val DefaultFee: String,
    val TenMinutesFee: String
)