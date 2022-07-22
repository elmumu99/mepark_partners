package com.mrpark1.meparkpartner.data.model.payment

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetChargingHistoryRequest(
    val StartDate: String,
    val EndDate: String
)