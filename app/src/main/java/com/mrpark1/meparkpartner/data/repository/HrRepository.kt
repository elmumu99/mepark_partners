package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.common.PartnerUser
import com.mrpark1.meparkpartner.data.model.common.PartnerUserCommutingHistory
import com.mrpark1.meparkpartner.data.model.partner.*
import retrofit2.Response

interface HrRepository {
    suspend fun getMyPartnerUsers(getMyPartnerUserRequest: GetMyPartnerUserRequest): Response<List<PartnerUser>>

    suspend fun getMyPartnerUsersCommutingHistory(getMyPartnerUsersCommutingHistoryRequest: GetMyPartnerUsersCommutingHistoryRequest): Response<List<PartnerUserCommutingHistory>>

    suspend fun removeMyEmployee(removeMyEmployeeRequest: RemoveMyEmployeeRequest): Response<RemoveMyEmployeeResponse>

    suspend fun addNewEmployee(addNewEmployeeRequest: AddNewEmployeeRequest): Response<AddNewEmployeeResponse>

    suspend fun updateMyEmployee(updateMyEmployeeRequest: UpdateMyEmployeeRequest): Response<UpdateMyEmployeeResponse>
}