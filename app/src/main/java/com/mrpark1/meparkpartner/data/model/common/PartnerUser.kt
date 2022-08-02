package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class PartnerUser(
    val UserID: String,
    val Role: String,
    val StartDate: String,
    val PartnerBN: String,
    val Email: String,
    val Birth: String,
    val Contact: String,
    val Name: String,
    val CommutingStatus: String,
    val StartDateTime: String,
    val EndTime: String,
    val Salary: String,
) : Serializable