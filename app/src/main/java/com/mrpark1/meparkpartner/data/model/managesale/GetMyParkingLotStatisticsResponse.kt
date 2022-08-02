package com.mrpark1.meparkpartner.data.model.managesale

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMyParkingLotStatisticsResponse(
    val TotalProfit: Int,
    val TotalIn: Int, // 전체 들어온 차량 수
    val TotalReturn: Int, // 전체 회차 수
    val Payment1: Int, //현금 결제
    val Payment2: Int, // 간편 결제
    val Payment0: Int, // 미 결제
    val VisitPlaces: List<VisitPlaceData>
)
