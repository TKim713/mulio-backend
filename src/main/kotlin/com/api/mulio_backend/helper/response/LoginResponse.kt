package com.api.mulio_backend.helper.response

data class LoginResponse(
    val userId: String,
    val cartId: String,
    val token: JwtResponse
)
