package com.api.mulio_backend.repository

import com.api.mulio_backend.model.Product
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : MongoRepository<Product, String> {
    fun findBySkuBaseAndColorAndSize(skuBase: String, color: String, size: String?): Product?
    fun findBySkuBaseAndColor(skuBase: String, color: String): Product?
    fun findBySkuCode(skuCode: String): Product?
    @Query("{ 'skuCode': { \$regex: '^?0-' } }")
    fun findBySkuBase(skuBase: String): List<Product>
    fun findByProductType(@Param("productType") productType: String): List<Product>
}