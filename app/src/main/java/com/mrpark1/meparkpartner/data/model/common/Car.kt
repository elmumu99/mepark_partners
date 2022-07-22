package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Car(
    val ParkingLN: String,
    val LP: String,
    val CarType: String,
    val VisitPlace: String,
    val Contact: String,
    val Memo: String,

    val Penalty: String,
    val Discount: String,
    val EnterDate: String,
    val Status: String,
    val Payment: String,
    val EnterTime: String,
    val User: String,
    val CarID: String,

    val ExitDate: String?,
    val ExitTime: String?,
    val ReturnDate: String?,
    val ReturnTime: String?,

    var expanded: Boolean = false,
    var serverTime: String = "",
    var parkTime: String = "",
    var timeFee: String = "",
    var finalFee: String = "",
    var defaultFee: String = "",
) : Serializable