package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "customer")
data class Customer(
    @Id
    val customerId: String,
    val userId: String,
    var fullName: String? = null,
    var phone: String? = null,
    var address: String? = null,
    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)
