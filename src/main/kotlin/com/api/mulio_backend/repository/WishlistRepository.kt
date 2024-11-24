package com.api.mulio_backend.repository

import com.api.mulio_backend.model.Wishlist
import org.springframework.data.mongodb.repository.MongoRepository

interface WishlistRepository : MongoRepository<Wishlist, String> {
    fun findByUserId(userId: String): Wishlist?
}