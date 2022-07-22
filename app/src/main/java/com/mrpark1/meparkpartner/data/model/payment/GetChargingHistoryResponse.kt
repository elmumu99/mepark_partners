package com.mrpark1.meparkpartner.data.model.payment

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetChargingHistoryResponse(
    val TotalCount: Int,
    val Result: List<CardPaymentInfo>,
)