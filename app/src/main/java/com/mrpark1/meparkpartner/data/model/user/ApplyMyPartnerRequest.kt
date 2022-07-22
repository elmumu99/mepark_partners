package com.mrpark1.meparkpartner.data.model.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApplyMyPartnerRequest(
    val PartnerBN : String,
    val ParkingLN: String
)
