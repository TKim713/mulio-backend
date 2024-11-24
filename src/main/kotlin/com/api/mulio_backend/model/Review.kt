package com.api.mulio_backend.model

import com.api.mulio_backend.serializer.ObjectIdSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class Review(
    @Id
    @JsonSerialize(using = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId.get(),
    @JsonSerialize(using = ObjectIdSerializer::class)
    val productId: ObjectId = ObjectId.get(),
    val userId: String,
    val rating: Int,
    val comment: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)