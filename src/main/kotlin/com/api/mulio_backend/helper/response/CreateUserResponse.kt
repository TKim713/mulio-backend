package com.api.mulio_backend.helper.response

data class CreateUserResponse(
    var username: String? = null,
    var email: String? = null,
    var role: String? = null,
    var token: String? = null
)
