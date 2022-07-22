package com.mrpark1.meparkpartner.data.repository.implementations

import com.mrpark1.meparkpartner.data.model.user.AddUserRequest
import com.mrpark1.meparkpartner.data.model.user.AddUserResponse
import com.mrpark1.meparkpartner.data.repository.NewUserRepository
import com.mrpark1.meparkpartner.data.service.UserService
import retrofit2.Response
import javax.inject.Inject

class NewUserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : NewUserRepository {

    override suspend fun addUser(addUserRequest: AddUserRequest): Response<AddUserResponse> =
        userService.addUser(addUserRequest)

}