package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "favorite")
data class Favorite(
    @Id
    val favoriteId: String?,
    val userId: String,
    val productId: String,
    val user: User,
    val product: Product,
    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)
