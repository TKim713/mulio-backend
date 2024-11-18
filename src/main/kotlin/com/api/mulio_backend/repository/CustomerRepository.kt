package com.api.mulio_backend.repository

import com.api.mulio_backend.model.Customer
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface CustomerRepository : MongoRepository<Customer, String> {
    fun findByUserId(userId: String): Optional<Customer>
}