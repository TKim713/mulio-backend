package com.api.mulio_backend.controller

import com.api.mulio_backend.helper.request.CreateUserRequest
import com.api.mulio_backend.helper.request.JwtRequest
import com.api.mulio_backend.helper.response.CreateUserResponse
import com.api.mulio_backend.helper.response.JwtResponse
import com.api.mulio_backend.helper.response.ResponseObject
import com.api.mulio_backend.service.AuthenticationService
import com.api.mulio_backend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthenticationController @Autowired constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) {

    @PostMapping("/authenticate")
    fun login(@RequestBody authenticationRequest: JwtRequest): ResponseEntity<ResponseObject<JwtResponse>> {
        return try {
            val jwtResponse = authenticationService.authenticate(authenticationRequest)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "Authentication successful", jwtResponse))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseObject(HttpStatus.UNAUTHORIZED.value(), "INVALID_CREDENTIALS", null))
        }
    }

    @PostMapping("/register")
    fun register(@RequestBody userRequest: CreateUserRequest): ResponseEntity<ResponseObject<CreateUserResponse>> {
        return try {
            val userResponse = userService.createUser(userRequest)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "User created successfully", userResponse))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Error creating user: ${e.message}", null))
        }
    }
}