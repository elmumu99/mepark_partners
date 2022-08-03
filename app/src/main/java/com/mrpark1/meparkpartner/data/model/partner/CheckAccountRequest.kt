package com.mrpark1.meparkpartner.data.model.partner

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckAccountRequest(
    val ACCT_NM: String, //예금주 이름
    val BANK_CD: String, // 은행코드
    val SEARCH_ACCT_NO: String, //계좌번호
    val ACNM_NO: String // 사업자 번호 , 주민등록상 생년월일 6자리
)
