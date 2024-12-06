package com.api.mulio_backend.helper.request

data class CustomerRequest(
    var fullName: String,
    var phone: String?,
    var address: String,
)
