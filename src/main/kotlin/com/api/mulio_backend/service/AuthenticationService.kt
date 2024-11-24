package com.api.mulio_backend.service

import com.api.mulio_backend.helper.request.JwtRequest
import com.api.mulio_backend.helper.response.JwtResponse
import com.api.mulio_backend.helper.response.LoginResponse

interface AuthenticationService {
    @Throws(Exception::class)
    fun authenticate(authenticationRequest: JwtRequest): LoginResponse
    fun logout(tokenStr: String)
    fun refreshToken(refreshToken: String): JwtResponse
}
