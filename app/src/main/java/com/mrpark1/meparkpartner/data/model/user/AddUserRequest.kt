package com.mrpark1.meparkpartner.data.model.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddUserRequest(
    val Issuer: String = "Google",
    val IDT: String,
    val Name: String,
    val Birth: String,
    val Contact: String
)