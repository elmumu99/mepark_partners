package com.mrpark1.meparkpartner.data.model.managesale

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ParkingLotData(
    val ParkingLN: String,
    val Name: String,
    val ParkingLotProfit: Int,
    val ParkingLotTotalIN: Int,
    val ParkingLotTotalReturn: Int
): Serializable
