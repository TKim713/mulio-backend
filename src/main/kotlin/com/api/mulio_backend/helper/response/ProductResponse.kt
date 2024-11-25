package com.api.mulio_backend.helper.response

data class ProductResponse(
    val productId: String? = null,
    val skuBase: String? = null,
    val skuCode: String? = null,
    val productName: String? = null,
    val price: Float? = null,
    val description: String? = null,
    val status: String? = null,
    val productType: String? = null,
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val images: List<String> = emptyList()
)