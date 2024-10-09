package com.api.mulio_backend.model

import com.api.mulio_backend.helper.enums.Role
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "user")
data class User(
    val username: String,
    val email: String,
    val password: String,
    val role: Role,
    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)
