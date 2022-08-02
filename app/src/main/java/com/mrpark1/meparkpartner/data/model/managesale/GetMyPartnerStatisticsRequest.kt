package com.mrpark1.meparkpartner.data.model.managesale

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMyPartnerStatisticsRequest(
    val StartDate: String,
    val EndDate: String
)
