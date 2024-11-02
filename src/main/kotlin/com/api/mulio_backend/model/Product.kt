package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "product")
data class Product(
    @Id
    var productId: String,
    val productName: String,
    val price: Float,
    val description: String,
    val size: String,
    val color: String,
    val amount: Int,
    val status: String,
    val productType: String,
    val image: String,
    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)
