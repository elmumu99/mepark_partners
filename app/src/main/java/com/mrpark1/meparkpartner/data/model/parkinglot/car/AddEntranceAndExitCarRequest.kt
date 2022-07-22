package com.mrpark1.meparkpartner.data.model.parkinglot.car

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddEntranceAndExitCarRequest(
    val ParkingLN: String,
    val LP: String,
    val CarType: String,
    val VisitPlace: String,
    val Contact: String,
    val Memo: String
)