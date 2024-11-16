package com.api.mulio_backend.helper.request

data class CreateProductRequest(
    var productName: String,
    var price: Float,
    var description: String,
    var size: String,
    var color: String,
    var amount: Int,
    var status: String,
    var productType: String,
    var image: String
)