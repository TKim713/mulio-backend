package com.api.mulio_backend.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "wishlist")
data class Wishlist(
    @Id
    val id: ObjectId = ObjectId.get(),
    val userId: String,
    val productIds: MutableList<String> = mutableListOf()
)