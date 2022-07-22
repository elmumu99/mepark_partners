package com.mrpark1.meparkpartner.data.model.payment

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class GetPointHistoryRequest (
    val StartDate: String,  //조회 시작 날짜 (이상) [YYYY-MM-DD hh:mm]
    val EndDate: String     //조회 마지막 날짜 (미만) [YYYY-MM-DD hh:mm]
)