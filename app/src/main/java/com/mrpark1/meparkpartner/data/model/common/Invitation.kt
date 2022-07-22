package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Invitation(
    val Salary: String,
    val PartnerBN: String,
    val PartnerName: String,
    val FirstDay: String
) : Serializable