package com.api.mulio_backend.helper.response

data class ResponseMessage<T>(
    val message: String,
    val data: T?
)