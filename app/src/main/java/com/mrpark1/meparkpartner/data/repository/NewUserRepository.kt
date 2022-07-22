package com.mrpark1.meparkpartner.data.repository

import com.mrpark1.meparkpartner.data.model.user.AddUserRequest
import com.mrpark1.meparkpartner.data.model.user.AddUserResponse
import retrofit2.Response

interface NewUserRepository {
    suspend fun addUser(addUserRequest: AddUserRequest): Response<AddUserResponse>
}