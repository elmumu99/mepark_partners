package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Partner(
    val InsuranceFee: String,
    val PartnerName: String,
    val BankName: String,
    val PartnerBN: String,
    val ApplyTimestamp: String,
    val Status: String,
    val PhoneNumber: String,
    val OwnerEmail: String,
    val OwnerName: String,
    val PaymentFee: String,
    val BankAccount: String,
    val Point: String,
    val CashFee: String
): Serializable
