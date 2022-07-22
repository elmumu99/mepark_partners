package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.common.PartnerUser
import com.mrpark1.meparkpartner.data.model.common.PartnerUserCommutingHistory
import com.mrpark1.meparkpartner.data.model.partner.*
import com.mrpark1.meparkpartner.data.repository.HrRepository
import com.mrpark1.meparkpartner.data.service.PartnerService
import retrofit2.Response
import javax.inject.Inject

class HrRepositoryImpl @Inject constructor(
    private val partnerService: PartnerService
    ): HrRepository{

    override suspend fun getMyPartnerUsers(getMyPartnerUserRequest: GetMyPartnerUserRequest): Response<List<PartnerUser>> =
        partnerService.getMyPartnerUsers(getMyPartnerUserRequest)

    override suspend fun getMyPartnerUsersCommutingHistory(
        getMyPartnerUsersCommutingHistoryRequest: GetMyPartnerUsersCommutingHistoryRequest
    ): Response<List<PartnerUserCommutingHistory>> =
        partnerService.getMyPartnerUsersCommutingHistory(getMyPartnerUsersCommutingHistoryRequest)

    override suspend fun removeMyEmployee(removeMyEmployeeRequest: RemoveMyEmployeeRequest): Response<RemoveMyEmployeeResponse> =
        partnerService.removeMyEmployee(removeMyEmployeeRequest)

    override suspend fun addNewEmployee(addNewEmployeeRequest: AddNewEmployeeRequest): Response<AddNewEmployeeResponse> =
        partnerService.addNewEmployee(addNewEmployeeRequest)

    override suspend fun updateMyEmployee(updateMyEmployeeRequest: UpdateMyEmployeeRequest): Response<UpdateMyEmployeeResponse> =
        partnerService.updateMyEmployee(updateMyEmployeeRequest)


}