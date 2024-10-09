package com.api.mulio_backend.service

import com.api.mulio_backend.helper.request.JwtRequest
import com.api.mulio_backend.helper.response.JwtResponse

interface AuthenticationService {
    @Throws(Exception::class)
    fun authenticate(authenticationRequest: JwtRequest): JwtResponse
}
