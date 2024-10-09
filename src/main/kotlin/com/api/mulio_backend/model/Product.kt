package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp

@Document(collection = "product")
data class Product(
    @Id
    val productId: Long? = null,
    val productName: String,
    val price: Float,
    val description: String,
    val size: String,
    val color: String,
    val amount: Int,
    val status: String,
    val productType: String,
    val image: String,
    val createdAt: Timestamp,
    var updatedAt: Timestamp? = null,
    var deletedAt: Timestamp? = null
)
