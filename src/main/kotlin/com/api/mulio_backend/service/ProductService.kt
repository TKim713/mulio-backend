package com.api.mulio_backend.service

import com.api.mulio_backend.model.Product
import com.api.mulio_backend.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun addProduct(product: Product): Product {
        return productRepository.save(product)
    }

    fun updateProduct(id: Long, product: Product): Product {
        val existingProduct = productRepository.findById(id.toString()).orElseThrow {
            throw IllegalArgumentException("Product with id $id not found")
        }
        // Update fields of existingProduct with values from product
        return productRepository.save(existingProduct)
    }

    fun deleteProduct(id: Long) {
        productRepository.deleteById(id.toString())
    }
}