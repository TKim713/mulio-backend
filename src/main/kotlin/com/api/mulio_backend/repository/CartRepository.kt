package com.api.mulio_backend.repository

import com.api.mulio_backend.model.Cart
import org.springframework.data.mongodb.repository.MongoRepository

interface CartRepository : MongoRepository<Cart, String> {
    fun findByUserId(userId: String): Cart?
}