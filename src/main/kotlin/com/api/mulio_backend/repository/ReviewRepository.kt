package com.api.mulio_backend.repository

import com.api.mulio_backend.model.Review
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ReviewRepository : MongoRepository<Review, ObjectId> {
    fun findByProductId(productId: String): List<Review>
}