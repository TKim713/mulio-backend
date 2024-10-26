package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.JwtTokenUtil
import com.api.mulio_backend.config.JwtUserDetailsService
import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.JwtRequest
import com.api.mulio_backend.helper.response.JwtResponse
import com.api.mulio_backend.model.User
import com.api.mulio_backend.repository.UserRepository
import com.api.mulio_backend.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Private

@Service
class AuthenticationServiceImpl @Autowired constructor(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil,
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val userRepository: UserRepository
) : AuthenticationService {

    @Throws(CustomException::class)
    override fun authenticate(authenticationRequest: JwtRequest): JwtResponse {
        try {
            // Kiểm tra người dùng đã kích hoạt tài khoản hay chưa
            val user = userRepository.findByEmail(authenticationRequest.email)
            if (user != null && !user.enabled) {
                throw CustomException("ACCOUNT_NOT_VERIFIED", HttpStatus.FORBIDDEN)
            }

            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    authenticationRequest.email,
                    authenticationRequest.password
                )
            )
        } catch (e: BadCredentialsException) {
            throw CustomException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED)
        } catch (e: CustomException) {
            throw e;
        } catch (e: Exception) {
            throw CustomException(e.message ?: "Authentication failed", HttpStatus.BAD_REQUEST)
        }

        val userDetails: UserDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.email)
        val token: String = jwtTokenUtil.generateToken(userDetails)

        return JwtResponse(token)
    }
}