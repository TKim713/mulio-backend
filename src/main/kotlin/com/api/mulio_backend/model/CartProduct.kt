package com.api.mulio_backend.model

import org.springframework.data.annotation.Id

data class CartProduct(
    @Id
    val cartProductId: String,
    val userId: String,
    val productId: String,
    var amount: Int,
)
