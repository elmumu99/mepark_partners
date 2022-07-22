package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class CommutingHistory (
    val UserID: String,
    val AMID: String, //출근ID
    val StartDate: String,
    val StartTime: String,
    val EndDate: String,
    val EndTime: String,
    val ParkingLN: String
): Serializable