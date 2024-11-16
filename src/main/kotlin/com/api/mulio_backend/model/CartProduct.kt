package com.api.mulio_backend.model

data class CartProduct(
    val productId: String,
    val price: Float,
    var amount: Int
)
