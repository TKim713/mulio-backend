package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.JwtTokenUtil
import com.api.mulio_backend.config.JwtUserDetailsService
import com.api.mulio_backend.helper.enums.TokenType
import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.JwtRequest
import com.api.mulio_backend.helper.response.JwtResponse
import com.api.mulio_backend.helper.response.LoginResponse
import com.api.mulio_backend.model.Token
import com.api.mulio_backend.model.User
import com.api.mulio_backend.repository.CartRepository
import com.api.mulio_backend.repository.TokenRepository
import com.api.mulio_backend.repository.UserRepository
import com.api.mulio_backend.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationServiceImpl @Autowired constructor(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil,
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val cartRepository: CartRepository
) : AuthenticationService {

    private val now: Date = Date()

    @Throws(CustomException::class)
    override fun authenticate(authenticationRequest: JwtRequest): LoginResponse {
        val user: User?
        try {
            // Kiểm tra người dùng đã kích hoạt tài khoản hay chưa
            user = userRepository.findByEmail(authenticationRequest.email)
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

        user ?: throw CustomException("USER_NOT_FOUND", HttpStatus.NOT_FOUND)
        val userDetails: UserDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.email)
        val accessToken = jwtTokenUtil.generateToken(userDetails)
        val refreshToken = jwtTokenUtil.generateRefreshToken(userDetails)

        val newToken = Token(
            tokenId = UUID.randomUUID().toString(),
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = TokenType.BEARER,
            expired = false,
            revoked = false,
            user = userDetails.username,
            createdAt = now,
        )
        tokenRepository.save(newToken)

        val cartId = cartRepository.findByUserId(user.userId)?.cartId ?: "CartNotFound"

        return LoginResponse(
            userId = user.userId,
            cartId = cartId,
            token = JwtResponse(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )
    }

    override fun logout(tokenStr: String) {
        val token = tokenRepository.findByAccessToken(tokenStr)
        if (token != null) {
            token.expired = true
            token.revoked = true
            token.updatedAt = now
            token.deletedAt = now
            tokenRepository.save(token)
        } else {
            throw CustomException("Token not found", HttpStatus.NOT_FOUND)
        }
    }

    override fun refreshToken(refreshToken: String): JwtResponse {
        if (!jwtTokenUtil.validateRefreshToken(refreshToken)) {
            val token = tokenRepository.findByRefreshToken(refreshToken)
            token?.apply {
                expired = true
                revoked = true
                tokenRepository.save(this)
            }
            throw CustomException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED)
        }

        val token = tokenRepository.findByRefreshToken(refreshToken)
            ?: throw CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED)

        val userDetails: UserDetails = jwtUserDetailsService.loadUserByUsername(token.user)
        val newAccessToken = jwtTokenUtil.generateToken(userDetails)

        token.accessToken = newAccessToken
        token.updatedAt = Date()
        tokenRepository.save(token)

        return JwtResponse(
            accessToken = newAccessToken,
            refreshToken = refreshToken
        )
    }
}