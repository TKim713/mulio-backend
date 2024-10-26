package com.api.mulio_backend.helper.response

import com.api.mulio_backend.model.CartProduct

data class CartResponse(
    var cartId: String? = null,
    var userId: String? = null,
    var products: List<CartProduct>,
    var totalNumber: Int,
    var totalPrice: Float,
) {
    // No-argument constructor provided by default if using data class with default values.
    constructor() : this(null, null, emptyList(), 0, 0f)
}
