package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "cart")
data class Cart(
    @Id
    val cartId: String,
    val userId: String,
    val products: List<CartProduct> = listOf(),
    val totalNumber: Int,
    val totalPrice: Float,
    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)
