package com.api.mulio_backend.model

data class ProductDetailsResponse(
    val productId: String,
    val name: String,
    val price: Float,
    val description: String,
    // Add other fields as needed
)