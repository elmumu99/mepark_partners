package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ParkingLot(
    val OtherPlaceFee: String,
    val DayParkFee: String,
    val PartnerBN: String,
    val MonthParkFee: String,
    val Spaces: String,
    val MinLeftSpaces: String,
    val Name: String,
    val ParkingLN: String,
    val ParkEnabled: Boolean = true,
    val VisitPlaces: List<VisitPlace>,
    val Insurance: String? = null,
    val Address: String?,
    val ImgUrl: String,
    val ParkedCarCount: Int
) : Serializable