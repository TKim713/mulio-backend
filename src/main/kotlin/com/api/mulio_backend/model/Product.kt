package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.bson.types.ObjectId
import java.util.*

@Document(collection = "product")
data class Product(
    @Id
    val productId: ObjectId = ObjectId.get(),
    var productName: String,
    var price: Float,
    var description: String,
    var size: String,
    var color: String,
    var amount: Int,
    var status: String,
    var productType: String,
    var image: String,
    var createdAt: Date,
    var updatedAt: Date? = null,
    val deletedAt: Date? = null
)