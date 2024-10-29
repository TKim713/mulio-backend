package com.api.mulio_backend.helper.response

data class CartProductResponse(
    val productName: String? = null,
    val price: Float? = null,
    val description: String? = null,
    val size: String? = null,
    val color: String? = null,
    val amount: Int? = null,
    val productType: String? = null,
    val image: String? = null,
)
