package com.api.mulio_backend.controller

import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CreateUserRequest
import com.api.mulio_backend.helper.request.JwtRequest
import com.api.mulio_backend.helper.response.CreateUserResponse
import com.api.mulio_backend.helper.response.JwtResponse
import com.api.mulio_backend.helper.response.LoginResponse
import com.api.mulio_backend.helper.response.ResponseObject
import com.api.mulio_backend.service.AuthenticationService
import com.api.mulio_backend.service.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthenticationController @Autowired constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) {

    @PostMapping("/authenticate")
    fun login(@RequestBody authenticationRequest: JwtRequest): ResponseEntity<ResponseObject<LoginResponse>> {
        return try {
            val loginResponse = authenticationService.authenticate(authenticationRequest)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "Authentication successful", loginResponse))
        } catch (e: CustomException) {
            // Handle custom exceptions based on their specific HTTP status
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), e.message ?: "Error during authentication", null))
        } catch (e: Exception) {
            // Generic exception handling for other cases
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "An unexpected error occurred: ${e.message}", null))
        }
    }

    @PostMapping("/register")
    fun register(@Valid @RequestBody userRequest: CreateUserRequest): ResponseEntity<ResponseObject<CreateUserResponse>> {
        return try {
            val userResponse = userService.createUser(userRequest)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "User created successfully", userResponse))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), "${e.message}", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Error creating user: ${e.message}", null))
        }
    }

    @GetMapping("/verify")
    fun verifyUser(@RequestParam token: String): ResponseEntity<ResponseObject<String>> {
        val responseObject = userService.verifyEmail(token)
        return ResponseEntity.status(responseObject.status)
            .body(responseObject)
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") token: String): ResponseEntity<ResponseObject<String>> {
        val jwtToken = token.replace("Bearer ", "")
        return try {
            authenticationService.logout(jwtToken)
            ResponseEntity.ok(ResponseObject(HttpStatus.OK.value(), "Logout successfully", null))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), e.message ?: "Error during logout", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "An unexpected error occurred: ${e.message}", null))
        }
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody refreshTokenRequest: Map<String, String>): ResponseEntity<ResponseObject<JwtResponse>> {
        return try {
            val refreshToken = refreshTokenRequest["refreshToken"]
                ?: throw CustomException("Refresh token is required", HttpStatus.BAD_REQUEST)

            val jwtResponse = authenticationService.refreshToken(refreshToken)
            ResponseEntity.ok(ResponseObject(HttpStatus.OK.value(), "Token refreshed successfully", jwtResponse))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), e.message ?: "Error refreshing token", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Unexpected error: ${e.message}", null))
        }
    }
}