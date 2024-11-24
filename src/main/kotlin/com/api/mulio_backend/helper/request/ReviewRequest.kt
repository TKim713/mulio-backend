package com.api.mulio_backend.helper.request

class ReviewRequest(
    val userId: String,
    val rating: Int,
    val comment: String
)