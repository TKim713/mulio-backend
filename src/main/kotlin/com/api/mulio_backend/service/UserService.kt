package com.api.mulio_backend.service

import com.api.mulio_backend.helper.request.CreateUserRequest
import com.api.mulio_backend.helper.response.CreateUserResponse
import com.api.mulio_backend.helper.response.UserResponse

interface UserService {
    fun createUser(userRequest: CreateUserRequest): CreateUserResponse
    fun verifyEmail(tokenStr: String): String
    fun getUser(tokenStr: String): UserResponse
}