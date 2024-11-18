package com.api.mulio_backend.helper.response

data class CartResponse(
    var cartId: String? = null,
    var products: List<CartProductResponse> = emptyList(),
    var totalNumber: Int = 0,
    var totalPrice: Float = 0f,
)
