package com.api.mulio_backend.helper.response

import java.time.LocalDateTime

data class ReviewResponse(
    val id: String? = null,
    val productId: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val rating: Int? = null,
    val comment: String? = null,
    var images: List<String> = emptyList(),
    val createdAt: String? = null
)
