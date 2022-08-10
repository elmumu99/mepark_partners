package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class User(
    val UserID: String,
    val Birth: String,
    val PartnerBN: String,
    val Email: String,
    val Name: String,
    val Contact: String,
    val Invitation: Invitation?,
    val PartnerStatus: String?,
    val Role: String?,
    var CommutingStatus: String = "1",// 출근 상태 1: 근무한적 없음 2: 근무중 3: 퇴근
) : Serializable