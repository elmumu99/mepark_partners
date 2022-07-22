package com.mrpark1.meparkpartner.data.model.payment

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CardPaymentInfo(
    val P_FN_NM: String, // 카드사
    val P_CARD_NUM: String, // 카드 번호 (가상계좌 입금시 없음)
    val P_RMESG2: String,  // 할부개월 (가상계좌 입금시 없음)
    val P_AUTH_DT: String, // 승인 일자
    val P_AMT: String, //결제금액
    val P_STATUS: String, // 가상계좌 입금 완료 02 , 카드 입금 완료 00
    val P_TID: String, // 승인거래번호
    val P_OID: String, // 주문번호
    val P_UNAME: String //구매자명
)