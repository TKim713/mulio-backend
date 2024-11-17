package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "order")
data class Order(
    @Id
    val orderId: String,
    val userId: String,
    val fullName: String,
    val phone: String,
    val address: String,
    val city: String,
    val district: String,
    val ward: String,
    val paymentMethod: String,
    val totalPrice: Float,
    val orderDate: Date,
    val orderProduct: List<CartProduct>,
    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)
