package com.api.mulio_backend.repository

import com.api.mulio_backend.model.Product
import org.springframework.data.mongodb.repository.MongoRepository

interface ProductRepository : MongoRepository<Product, String> {
    fun findByProductNameAndColor(productName: String, color: String): Product?
}