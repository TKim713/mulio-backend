package com.api.mulio_backend.model

import com.api.mulio_backend.helper.enums.TokenType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "token")
data class Token(
    @Id
    val tokenId: String,
    val token: String,

    val tokenType: TokenType? = null,

    var expired: Boolean = false, // Xác định token có hết hạn không
    var revoked: Boolean = false, // Xác định token có bị thu hồi không

    val user: String,

    val createdAt: Date,
    var updatedAt: Date? = null,
    var deletedAt: Date? = null
)