package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class PartnerUserCommutingHistory (
    val UserID: String,
    val Email: String,
    val Name: String,
    val TotalCount: Int,
    val CommuntingHistory: List<CommutingHistory>
): Serializable
