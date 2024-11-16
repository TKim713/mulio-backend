package com.api.mulio_backend.repository

import com.api.mulio_backend.model.Product
import org.springframework.data.mongodb.repository.MongoRepository

interface ProductRepository : MongoRepository<Product, String> {
    fun findByProductNameAndColorAndSize(productName: String, color: String, size: String): Product?
}