package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp
import java.util.*

@Document(collection = "favorite")
data class Favorite(
    @Id
    val favoriteId: Long? = null,
    val userId: Long,
    val productId: Long,
    val user: User,
    val product: Product,
    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)
