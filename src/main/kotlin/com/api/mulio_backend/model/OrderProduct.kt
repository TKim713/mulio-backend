package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

data class OrderProduct(
    val orderId: String,
    val productId: String,
    val price: Float,
    val amount: Int
)
