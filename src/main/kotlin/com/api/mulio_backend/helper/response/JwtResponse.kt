package com.api.mulio_backend.helper.response

data class JwtResponse(
    val accessToken: String,
    val refreshToken: String? = null // Optional for cases where only an access token is needed
)

