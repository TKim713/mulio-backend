package com.api.mulio_backend.helper.request

data class CheckoutRequest(
    var fullName: String,
    var phone: String,
    var address: String,
    var city: String,
    var district: String,
    var ward: String,
    var paymentMethod: String
)
