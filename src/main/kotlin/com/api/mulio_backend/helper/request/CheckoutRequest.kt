package com.api.mulio_backend.helper.request

import com.api.mulio_backend.model.CartProduct

data class CheckoutRequest(
    var userId: String,
    var totalPrice: Float,
    var itemsToCheckout: List<CartProduct>
)
