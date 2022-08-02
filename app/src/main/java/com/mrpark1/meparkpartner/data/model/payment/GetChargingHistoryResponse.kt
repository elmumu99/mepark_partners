package com.mrpark1.meparkpartner.data.model.payment

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetChargingHistoryResponse(
    val TotalCount: Int, //기간내 총 내역
    val VBANKCount: Int, // 가상계좌 입금 카운트
    val CARDCount: Int, //카드 입금 카운트
    val TotalAmount: Int, //총 금액
    val Result: List<CardPaymentInfo>,
)