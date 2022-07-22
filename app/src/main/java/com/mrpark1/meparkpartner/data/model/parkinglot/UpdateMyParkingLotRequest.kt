package com.mrpark1.meparkpartner.data.model.parkinglot

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateMyParkingLotRequest(
    val ParkingLN: String,
    val Name: String,
    val Address: String,
    val Insurance: String,
    val Photo: String,
    val Spaces: String,
    val MinLeftSpaces: String,
    val OtherPlaceFee: String,
    val DayParkFee: String,
    val MonthParkFee: String,
    val ParkEnabled: Boolean,
)