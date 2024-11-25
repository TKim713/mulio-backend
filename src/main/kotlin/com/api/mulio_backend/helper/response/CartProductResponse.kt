package com.api.mulio_backend.helper.response

data class CartProductResponse(
    val productId: String? = null,
    val skuBase: String? = null,
    val skuCode: String? = null,
    val productName: String? = null,
    val price: Float? = null,
    val description: String? = null,
    val size: String? = null,
    val color: String? = null,
    val amount: Int? = null,
    val productType: String? = null,
    val image: List<String> = emptyList(),
    val totalPrice: Float? = null,
)
