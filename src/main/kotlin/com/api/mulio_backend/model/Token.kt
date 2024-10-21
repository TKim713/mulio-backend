package com.api.mulio_backend.model

import com.api.mulio_backend.helper.enums.TokenType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "token")
data class Token(
    @Id
    val tokenId: String? = null,
    val token: String,

    val tokenType: TokenType,

    var expired: Boolean = false, // Xác định token có hết hạn không
    val revoked: Boolean = false, // Xác định token có bị thu hồi không

    val userId: String,

    val createdAt: Date,
    val updatedAt: Date? = null,
    val deletedAt: Date? = null
)