package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "cart")
data class Cart(
    @Id
    val cartId: String,
    val userId: String,
    var products: List<CartProduct> = listOf(),
    var totalNumber: Int,
    var totalPrice: Float,
    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)
