package com.api.mulio_backend.service

import com.api.mulio_backend.helper.request.CreateUserRequest
import com.api.mulio_backend.helper.response.CreateUserResponse

interface UserService {
    fun createUser(userRequest: CreateUserRequest): CreateUserResponse
}