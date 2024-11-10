package com.api.mulio_backend.helper.response

import java.util.Date

data class ProductResponse(
    val productId: String,
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
    var updatedAt: Date?,
    var deletedAt: Date?
)