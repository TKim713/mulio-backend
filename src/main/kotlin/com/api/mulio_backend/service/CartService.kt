package com.api.mulio_backend.service

import com.api.mulio_backend.helper.request.CheckoutRequest
import com.api.mulio_backend.helper.request.AddProductToCartRequest
import com.api.mulio_backend.helper.response.CartResponse
import com.api.mulio_backend.model.Order

interface CartService {
    fun addToCart(addProductToCartRequest: AddProductToCartRequest): CartResponse
    fun getCartByUserId(userId: String): CartResponse
    fun checkout(userId: String, checkoutRequest: CheckoutRequest): Order
}