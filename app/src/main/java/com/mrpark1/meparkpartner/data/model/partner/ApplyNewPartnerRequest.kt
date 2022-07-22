package com.mrpark1.meparkpartner.data.model.partner

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApplyNewPartnerRequest(
    val PartnerBN: String,
    val PhoneNumber: String,
    val PartnerName: String,
    val OwnerName: String,
    val BankAccount: String,
    val BankName: String,
    val BRPhoto: String
)