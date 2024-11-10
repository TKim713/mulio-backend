package com.api.mulio_backend.service

import com.api.mulio_backend.model.Product
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductService {
    fun createProduct(product: Product): Product
    fun getProductById(productId: ObjectId): Product?
    fun updateProduct(productId: String, product: Product): Product?
    fun deleteProduct(productId: ObjectId): Boolean
    fun getAllProducts(): List<Product>
    fun getProducts(pageable: Pageable): Page<Product> // New method for pagination
}