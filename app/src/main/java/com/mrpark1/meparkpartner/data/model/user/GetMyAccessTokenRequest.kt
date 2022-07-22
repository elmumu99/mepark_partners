package com.mrpark1.meparkpartner.data.model.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMyAccessTokenRequest(
    val Issuer: String = "Google",
    val IDT: String
)