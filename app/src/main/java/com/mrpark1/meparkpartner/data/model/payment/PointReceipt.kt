package com.mrpark1.meparkpartner.data.model.payment

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PointReceipt(
    val TotalTime: String ,  //총 주차시간 분단위
    val PointID: String,  // 사용내역 ID
    val InsuranceFee: String, // 보험료
    val LP: String,   // 차번호
    val ExitDateTime: String , // 출차시간 "yyyy-MM-dd hh:mm"
    val PartnerBN: String , // 파트너번호
    val CarType: String , // 차종류
    val Insurance: String , // 보험 종류 A: 일반형, B: 렌트형, N:보험미사용
    val EnterDateTime: String , // 입차시간 "yyyy-MM-dd hh:mm"
    val Name: String // 주차장이름
)