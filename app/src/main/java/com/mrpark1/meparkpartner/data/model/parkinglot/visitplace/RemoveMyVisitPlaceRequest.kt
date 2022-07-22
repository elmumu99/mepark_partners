package com.mrpark1.meparkpartner.data.model.parkinglot.visitplace

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoveMyVisitPlaceRequest(
    val ParkingLN: String,
    val PlaceName: String,
)