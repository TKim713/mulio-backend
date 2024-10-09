package com.api.mulio_backend.helper.response

data class CreateUserResponse(
    val username: String,

    val email: String,

    val password: String,

    val role: String
)
