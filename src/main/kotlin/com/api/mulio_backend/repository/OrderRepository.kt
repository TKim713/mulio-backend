package com.api.mulio_backend.repository

import com.api.mulio_backend.model.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface OrderRepository: MongoRepository<Order, String> {
    fun findByUserId(userId: String, pageable: Pageable): Page<Order>
}