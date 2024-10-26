package com.api.mulio_backend.helper.request

data class CreateCartRequest(
    var userId: String,
    var productId: String,
    var amount: Int
)
