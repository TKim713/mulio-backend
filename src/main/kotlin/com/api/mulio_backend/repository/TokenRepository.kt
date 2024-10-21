package com.api.mulio_backend.repository

import com.api.mulio_backend.model.Token
import org.springframework.data.mongodb.repository.MongoRepository

interface TokenRepository : MongoRepository<Token, String> {
    fun findByToken(token: String): Token?
}
