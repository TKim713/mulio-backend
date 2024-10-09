package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp

@Document(collection = "favorite")
data class Favorite(
    @Id
    val favoriteId: Long? = null,
    val userId: Long,
    val productId: Long,
    val user: User,
    val product: Product,
    val createdAt: Timestamp,
    var updatedAt: Timestamp? = null,
    var deletedAt: Timestamp? = null
)
