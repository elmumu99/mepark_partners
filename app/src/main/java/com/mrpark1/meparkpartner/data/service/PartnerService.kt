package com.mrpark1.meparkpartner.data.service

import com.mrpark1.meparkpartner.data.model.common.PartnerUser
import com.mrpark1.meparkpartner.data.model.common.PartnerUserCommutingHistory
import com.mrpark1.meparkpartner.data.model.partner.*
import com.mrpark1.meparkpartner.data.model.payment.GetChargingHistoryRequest
import com.mrpark1.meparkpartner.data.model.payment.GetChargingHistoryResponse
import com.mrpark1.meparkpartner.data.model.payment.GetPointHistoryRequest
import com.mrpark1.meparkpartner.data.model.payment.GetPointHistoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PartnerService {
    //파트너와 관련된 API interface를 모아둡니다.
    //파트너 정보 조회 등의 API 추가 시 이곳에 작성할 수 있습니다.

    @POST("/ApplyNewPartner")
    suspend fun applyNewPartner(@Body request: ApplyNewPartnerRequest): Response<ApplyNewPartnerResponse>

    @POST("/AddNewEmployee")
    suspend fun addNewEmployee(@Body request: AddNewEmployeeRequest): Response<AddNewEmployeeResponse>

    @POST("/GetMyPartnerUsers")
    suspend fun getMyPartnerUsers(@Body request: GetMyPartnerUserRequest): Response<List<PartnerUser>>

    @POST("/GetMyPartnerUsersCommutingHistory")
    suspend fun getMyPartnerUsersCommutingHistory(@Body request: GetMyPartnerUsersCommutingHistoryRequest): Response<List<PartnerUserCommutingHistory>>

    @POST("/GetChargingHistory")
    suspend fun getCharginHistory(@Body request: GetChargingHistoryRequest): Response<GetChargingHistoryResponse>

    @POST("/GetPointHistory")
    suspend fun getPointHistory(@Body request: GetPointHistoryRequest): Response<GetPointHistoryResponse>

    @POST("/UpdateMyEmployee")
    suspend fun updateMyEmployee(@Body request: UpdateMyEmployeeRequest): Response<UpdateMyEmployeeResponse>

    @POST("RemoveMyEmployee")
    suspend fun removeMyEmployee(@Body request: RemoveMyEmployeeRequest): Response<RemoveMyEmployeeResponse>
}