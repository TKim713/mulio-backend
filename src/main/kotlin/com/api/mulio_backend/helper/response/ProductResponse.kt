package com.api.mulio_backend.helper.response

data class ProductResponse(
    var skuBase: String? = null,
    var productName: String? = null,
    var price: Float? = null,
    var description: String? = null,
    var status: String? = null,
    var productType: String? = null,
    var averageRating: Number? = null,
    var totalRating: Int? = null,
    var sizes: List<String> = emptyList(),
    var colors: List<String> = emptyList(),
    var images: List<String> = emptyList()
)