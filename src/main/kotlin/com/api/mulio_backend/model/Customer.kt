package com.api.mulio_backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "customer")
data class Customer(
    @Id
    val customerId: String,
    var fullName: String,
    var phone: String,
    var address: String,
    var city: String,
    var district: String,
    var ward: String,
    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)
