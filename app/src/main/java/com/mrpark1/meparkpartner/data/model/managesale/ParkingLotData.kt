package com.mrpark1.meparkpartner.data.model.managesale

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ParkingLotData(
    var ParkingLN: String = "",
    var Name: String = "",
    var ParkingLotProfit: Int = 0,
    var ParkingLotTotalIN: Int = 0,
    var ParkingLotTotalReturn: Int = 0
): Serializable
