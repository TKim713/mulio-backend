package com.api.mulio_backend.helper.request

data class CreateProductRequest(
    var skuBase: String,
    var productName: String,
    var price: Float,
    var description: String,
    var size: String,
    var color: String,
    var amount: Int?=0,
    var status: String,
    var productType: String,
    var images: String
)