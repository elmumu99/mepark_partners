package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Car(
    var ParkingLN: String,
    var LP: String,
    var CarType: String,
    var VisitPlace: String,
    var Contact: String,
    var Memo: String,

    var Penalty: String,
    var Profit: String,
    var Discount: String,
    var EnterDate: String,
    var Status: String,
    var Payment: String,
    var EnterTime: String,
    var User: String,
    var CarID: String,

    var DefaultFee: String = "0",
    var TotalTime: String = "0",
    var ExitDate: String?,
    var ExitTime: String?,
    var ReturnDate: String?,
    var ReturnTime: String?,

    var expanded: Boolean = false,
    var serverTime: String = "",
    var parkTime: String = "",
    var timeFee: String = "",
    var finalFee: String = "",

    var created_at: String,
    var is_use: String,
    var updated_at: String
) : Serializable