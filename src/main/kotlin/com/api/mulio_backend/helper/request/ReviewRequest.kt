package com.api.mulio_backend.helper.request

class ReviewRequest(
    val rating: Int,
    val comment: String,
    val images: List<String> = emptyList(),
)