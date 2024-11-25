package com.api.mulio_backend.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import com.api.mulio_backend.serializer.ObjectIdSerializer

@Document(collection = "product")
data class Product(
    @Id
    @JsonSerialize(using = ObjectIdSerializer::class)
    val productId: ObjectId = ObjectId.get(),
    var skuBase: String,
    var skuCode: String,
    var productName: String,
    var price: Float,
    var description: String,
    var size: String? = null,
    var color: String,
    var amount: Int? = 0,
    var status: String,
    var productType: String,
    var images: List<String> = emptyList(),
    var createdAt: Date,
    var updatedAt: Date? = null,
    val deletedAt: Date? = null
)