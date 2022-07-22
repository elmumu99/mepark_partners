package com.mrpark1.meparkpartner.data.model.payment

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class InicisResponse(
    val P_STATUS: String,
    val P_RMESG1: String,
    val P_TID: String,
    val P_REQ_URL: String="",
    val P_NOTI: String,
    val P_AMT: String,
)