package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "order_product")
data class OrderProduct(
    @Id
    val orderProductId: String,
    val orderId: String,
    val productId: String,
    val price: Float,
    val amount: Int,
    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)
