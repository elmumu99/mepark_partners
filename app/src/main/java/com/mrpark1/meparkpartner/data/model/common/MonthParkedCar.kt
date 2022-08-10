package com.mrpark1.meparkpartner.data.model.common

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MonthParkedCar (
    val Profit: String,//계약금
    val MonthCarID: String,// 월주차 고유 ID
    val created_at: String,
    val LP: String,//차번호
    val StartDate: String, // 계약 시작 일
    val is_use: String,
    val Memo: String, //메모
    val EndDate: String,// 계약 종료 일
    val Contact: String,//연락처
    val ParkingLN: String,// 주차장 고유 ID
    val Payment: String,// 0이면 미결제 , 1이면 간편결제, 2면 현금, 3 카드
    val updated_at: String,
    val User: String,//0 이면 고객 앱 미사용자, 1이면 사용
)