package com.api.mulio_backend.model

data class CartProduct(
    val productId: String,
    var totalPrice: Float,
    var totalAmount: Int
)
