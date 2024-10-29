package com.api.mulio_backend.helper.response

data class CartResponse(
    var products: List<CartProductResponse> = emptyList(),
    var totalNumber: Int = 0,
    var totalPrice: Float = 0f,
)
//{
//    constructor() : this(emptyList(), 0, 0f)
//}
