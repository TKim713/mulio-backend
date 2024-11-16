package com.api.mulio_backend.helper.request

data class CheckoutRequest(
    var totalPrice: Float,
    var itemsToCheckout: List<CartProductRequest>
)
