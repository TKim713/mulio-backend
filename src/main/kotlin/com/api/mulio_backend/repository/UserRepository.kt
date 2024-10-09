package com.api.mulio_backend.repository

import com.api.mulio_backend.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
}