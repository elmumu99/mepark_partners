package com.mrpark1.meparkpartner.data.model.partner

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddNewEmployeeRequest(
    val PartnerBN: String,
    val Email: String,
    val ParkingLN: String,
    val StartDate: String,
    val Salary: String
)
