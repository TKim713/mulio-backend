package com.api.mulio_backend.helper.request

data class AddProductToCartRequest(
    var productName: String,
    var color: String,
    var size: String,
    var amount: Int,
)
