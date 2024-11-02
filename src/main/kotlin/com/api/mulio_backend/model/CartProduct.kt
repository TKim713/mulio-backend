package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "cart_product")
data class CartProduct(
    @Id
    val cartProductId: String,
    val cartId: String,
    val productId: String,
    val price: Float,
    var amount: Int,
    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)
