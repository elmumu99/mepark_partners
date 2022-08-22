package com.mrpark1.meparkpartner.util

import com.mrpark1.meparkpartner.data.model.common.ParkingLot


object Constants {
    //상수 정보를 담는 클래스

    //base url
    const val BASE_URL = "https://eclv33ar05.execute-api.ap-northeast-2.amazonaws.com/"
    //구글 관련 Client ID (Firebase, GCP, 구글로그인 등)
    const val SERVER_CLIENT_ID =
        "378481394591-24chp26t4eapi9bnm92j831an0lencnt.apps.googleusercontent.com"
    const val TAG = "debug"

    const val isTestMode = false
    const val testToken = "ZDY4YjIyOWItZmJkNC00MmY1LWE0YmUtNjVmOTZjOWZhYzNm"

    var IDT = ""
    var PartnerBN = ""
    var parkingLots = ArrayList<ParkingLot>()
    var isAdmin = false
    var CommutingStatus = "0" // 출근 상태 1: 근무한적 없음 2: 근무중 3: 퇴근  0: 초기화 전
    lateinit var selectedParkingLot : ParkingLot

}