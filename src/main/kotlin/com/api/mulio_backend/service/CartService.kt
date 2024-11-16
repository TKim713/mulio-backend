package com.api.mulio_backend.service

import com.api.mulio_backend.helper.request.CheckoutRequest
import com.api.mulio_backend.helper.request.AddProductToCartRequest
import com.api.mulio_backend.helper.response.CartResponse
import com.api.mulio_backend.model.Order

interface CartService {
    fun addToCart(cartId: String, addProductToCartRequest: AddProductToCartRequest): CartResponse
    fun getCartByUserId(userId: String): CartResponse
    fun checkout(cartId: String, checkoutRequest: CheckoutRequest): Order
}