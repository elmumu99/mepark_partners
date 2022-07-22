package com.mrpark1.meparkpartner.data.model.partner

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoveMyEmployeeRequest(
    val UserID: String
)
