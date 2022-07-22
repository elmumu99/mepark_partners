package com.mrpark1.meparkpartner.data.model.parkinglot.car

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateEntranceAndExitCarRequest(
    val Mode: String,
    val ParkingLN: String,
    val CarID: String,
    val Penalty: String? = null,
    val Discount: String? = null,
    val LP: String? = null,
    val Memo: String? = null,
    val EnterDate: String? = null,
    val Contact: String? = null,
    val EnterTime: String? = null,
    val CarType: String? = null,
    val VisitPlace: String? = null,
    val Payment: String? = null,
)