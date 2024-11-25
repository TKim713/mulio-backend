package com.api.mulio_backend.helper.response

data class ProductDetailsResponse(
    val productId: String,
    val name: String,
    val price: Float,
    val description: String,
    // Add other fields as needed
)