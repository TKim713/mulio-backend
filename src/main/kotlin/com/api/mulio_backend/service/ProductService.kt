package com.api.mulio_backend.service

import com.api.mulio_backend.helper.request.CreateProductRequest
import com.api.mulio_backend.helper.response.ProductResponse
import com.api.mulio_backend.model.Product
import com.api.mulio_backend.model.Review
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductService {
    fun createProduct(createProductRequest: CreateProductRequest): Product?
    fun getProductById(productId: ObjectId): Product?
    fun updateProduct(productId: String, updateProductRequest: CreateProductRequest): Product?
    fun deleteProduct(productId: ObjectId): Boolean
    fun getAllProducts(): List<Product>
    fun getProducts(pageable: Pageable): Page<Product>
    fun getProductsBySkuBase(skuBase: String): List<ProductResponse>
    fun getProductByProductType(productType: String): List<Product>
    fun getProductBySkuBaseAndAttributes(skuBase: String, color: String, size: String?): Product?
    fun getListSizeBySkuBase(skuBase: String): List<String?>
    fun getListColorBySkuBase(skuBaseName: String): List<String>
    fun addToWishlist(userId: String, productId: String)
    fun getWishlist(userId: String): List<Product>
    fun addReview(productId: ObjectId, userId: String, rating: Int, comment: String): Review
    fun getReviewsByProductId(productId: ObjectId): List<Review>
}