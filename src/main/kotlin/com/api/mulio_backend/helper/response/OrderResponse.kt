package com.api.mulio_backend.helper.response

import java.util.*

data class OrderResponse(
    var orderId: String? = null,
    var fullName: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var city: String? = null,
    var district: String? = null,
    var ward: String? = null,
    var paymentMethod: String? = null,
    var totalPrice: Float? = null,
    var orderDate: Date? = null,
    var orderProduct: List<CartProductResponse> = emptyList()
)
