package com.api.mulio_backend.helper.response

data class CustomerResponse(
    var customerId: String? = null,
    var userId: String? = null,
    var fullName: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var address: String? = null,
)
