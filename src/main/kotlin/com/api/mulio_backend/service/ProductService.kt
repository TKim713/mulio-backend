package com.api.mulio_backend.service

import com.api.mulio_backend.helper.request.CreateProductRequest
import com.api.mulio_backend.helper.request.ReviewRequest
import com.api.mulio_backend.helper.response.ProductResponse
import com.api.mulio_backend.helper.response.ReviewResponse
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
    fun getProducts(pageable: Pageable, search: String?): Page<Product>
    fun getProductsBySkuBase(skuBase: String): List<ProductResponse>
    fun getProductByProductType(productType: String): List<Product>
    fun getProductBySkuBaseAndAttributes(skuBase: String, color: String, size: String?): Product?
    fun getListSizeBySkuBase(skuBase: String): List<String?>
    fun getListColorBySkuBase(skuBaseName: String): List<String>
    fun addToWishlistBySkuBase(tokenStr: String, skuBase: String): List<ProductResponse>
    fun getWishlist(tokenStr: String): List<ProductResponse>
    fun deleteFromWishlistBySkuBase(tokenStr: String, skuBase: String): List<ProductResponse>
    fun addReview(productId: ObjectId, reviewRequest: ReviewRequest): Review
    fun getReviewsByProductId(productId: ObjectId): List<Review>
    fun getReviewsBySkuBase(skuBase: String): List<ReviewResponse>
    fun mapToProductResponse(products: List<Product>, reviews: List<Review>): List<ProductResponse>
}