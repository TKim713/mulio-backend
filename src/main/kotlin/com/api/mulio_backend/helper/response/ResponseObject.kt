package com.api.mulio_backend.helper.response

data class ResponseObject<C>(
    var status: Int = 0,
    var message: String = "",
    var data: C? = null // Using nullable type for data
)

