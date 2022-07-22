package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class VisitPlace(
    val DefaultTime: String,
    val TenMinutesFee: String,
    val FreeTime: String,
    val PlaceName: String,
    val DefaultFee: String,
    val ParkingLN: String,
) : Serializable