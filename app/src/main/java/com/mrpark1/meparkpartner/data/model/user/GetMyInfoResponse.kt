package com.mrpark1.meparkpartner.data.model.user

import com.mrpark1.meparkpartner.data.model.common.Invitation
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMyInfoResponse(
    val UserID: String,
    val Birth: String,
    val PartnerBN: String,
    val Email: String,
    val Name: String,
    val Contact: String,
    val Invitation: Invitation?,
    val Status: String?
)