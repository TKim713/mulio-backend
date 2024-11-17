package com.api.mulio_backend.helper.response

import com.api.mulio_backend.helper.enums.Role

data class UserResponse(
    var userId: String? = null,
    var username: String? = null,
    var email: String? = null,
    var password: String? = null,
    var role: Role? = null,
    var enabled: Boolean = false,
)
