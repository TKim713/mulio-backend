package com.api.mulio_backend.helper.request

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CustomerRequest(
    var fullName: String,
    @field:Pattern(
        regexp = "^0[0-9]+$",
        message = "Phone number must start with 0 and contain only numeric characters"
    )
    @field:Size(
        min = 9,
        max = 10,
        message = "Phone number must be between 9 and 10 digits"
    )
    var phone: String,
    var address: String,
)
