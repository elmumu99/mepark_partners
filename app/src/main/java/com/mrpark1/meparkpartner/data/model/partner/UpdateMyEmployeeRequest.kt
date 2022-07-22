package com.mrpark1.meparkpartner.data.model.partner

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateMyEmployeeRequest(
    val UserID: String,
    val StartDate: String,
    val Role: String,
    val Salary: String
)
