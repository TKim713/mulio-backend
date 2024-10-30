package com.api.mulio_backend.helper.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateUserRequest(
    val username: String,

    @field:Email(message = "Email should be valid")
    @field:Pattern(
        regexp = "^[\\w.%+-]+@[\\w.-]+\\.(com|vn)$",
        message = "Email should be valid"
    )
    val email: String,

    @field:Size(min = 6, max = 18, message = "Password should be between 6 and 18 characters")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@\$!%*#?&]).*$",
        message = "Password should contain at least one lowercase, one uppercase, and one special character (@\$!%*#?&)"
    )
    val password: String,

    val role: String
)
