package com.api.mulio_backend.config

import com.api.mulio_backend.repository.TokenRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*

@Component
class JwtTokenUtil : Serializable {

    companion object {
        private const val serialVersionUID = -2550185165626007488L
        const val JWT_TOKEN_VALIDITY = 60 * 60
    }

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    @Value("\${jwt.secret}")
    lateinit var secret: String

    // Retrieve username from JWT token
    fun getUsernameFromToken(token: String): String {
        return getClaimFromToken(token, Claims::getSubject)
    }

    // Retrieve expiration date from JWT token
    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken(token, Claims::getExpiration)
    }

    private fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .body
    }

    // Check if the token has expired
    fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    private fun isTokenRevoked(token: String): Boolean {
        val savedToken = tokenRepository.findByToken(token)
        return savedToken != null && savedToken.revoked
    }

    // Generate token for user
    fun generateToken(userDetails: UserDetails): String {
        val claims = mutableMapOf<String, Any>()
        return doGenerateToken(claims, userDetails.username)
    }

    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

    fun generateRefreshToken(userDetails: UserDetails): String {
        return Jwts.builder()
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + (10 * 24 * 60 * 60 * 1000L)))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

    // Validate token
    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token) && !isTokenRevoked(token)
    }

    fun validateRefreshToken(refreshToken: String): Boolean {
        return try {
            val username = getUsernameFromToken(refreshToken)
            val isExpired = isTokenExpired(refreshToken)
            val isRevoked = isTokenRevoked(refreshToken)
            !isExpired && !isRevoked
        } catch (ex: Exception) {
            false
        }
    }
}