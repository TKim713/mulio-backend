package com.api.mulio_backend.helper.request

data class AddProductToCartRequest(
    var userId: String,
    var productName: String,
    var color: String,
    var amount: Int,
    var price: Float
)
