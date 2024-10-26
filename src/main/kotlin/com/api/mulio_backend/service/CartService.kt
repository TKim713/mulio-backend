package com.api.mulio_backend.service

import com.api.mulio_backend.helper.request.CreateCartRequest
import com.api.mulio_backend.helper.response.CartResponse

interface CartService {
    fun addToCart(createCartRequest: CreateCartRequest): CartResponse
    fun getCartByUserId(userId: String): CartResponse
}