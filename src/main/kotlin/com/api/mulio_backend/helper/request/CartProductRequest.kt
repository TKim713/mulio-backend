package com.api.mulio_backend.helper.request

data class CartProductRequest(
    val productId: String,
    var totalPrice: Float,
    var totalAmount: Int
)
