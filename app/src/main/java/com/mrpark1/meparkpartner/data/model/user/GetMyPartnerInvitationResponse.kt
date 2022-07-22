package com.mrpark1.meparkpartner.data.model.user

import com.mrpark1.meparkpartner.data.model.common.InvitationParkingLot
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMyPartnerInvitationResponse(
    val InviteDate: String,
    val UserID: String,
    val StartDate: String,
    val PartnerBN: String,
    val Salary: String,
    val ParkingLN: String,
    val ParkingLots: InvitationParkingLot
)
