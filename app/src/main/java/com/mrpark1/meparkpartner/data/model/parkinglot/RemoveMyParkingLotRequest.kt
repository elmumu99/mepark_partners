package com.mrpark1.meparkpartner.data.model.parkinglot

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoveMyParkingLotRequest(
    val ParkingLN: String,
)