package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp

@Document(collection = "cart")
data class Cart(
    @Id
    val cartId: Long? = null,
    val productId: Long,
    val userId: Long,
    val amount: Int,
    val user: User,
    val product: Product,
    val totalNumber: Int,
    val totalPrice: Float,
    val createdAt: Timestamp,
    var updatedAt: Timestamp? = null,
    var deletedAt: Timestamp? = null
)
