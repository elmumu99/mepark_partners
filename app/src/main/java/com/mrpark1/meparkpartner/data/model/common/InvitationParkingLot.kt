package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class InvitationParkingLot(
    val OtherPlaceFee: String,
    val DayParkFee: String,
    val PartnerBN: String,
    val MonthParkFee: String,
    val ParkEnabled: Boolean = true,
    val Spaces: String,
    val Insurance: String? = null,
    val MinLeftSpaces: String,
    val Name: String,
    val ParkingLN: String
) : Serializable
