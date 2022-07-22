package com.mrpark1.meparkpartner.data.model.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserEmailRequest(
    val Issuer: String = "Google",
    val IDT: String,
    val Email: String,
    val AuthCode: String,
)