package com.mrpark1.meparkpartner.data.model.managesale

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMyPartnerStatisticsResponse(
    var TotalProfit: Int = 0,
    var TotalIn: Int = 0, // 전체 들어온 차량 수
    var TotalReturn: Int = 0, // 전체 회차 수
    var Payment1: Int = 0, //현금 결제
    var Payment2: Int = 0, // 간편 결제
    var Payment0: Int = 0, // 미 결제
    var ParkingLots: List<ParkingLotData>
)
