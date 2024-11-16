package com.api.mulio_backend.service

import com.api.mulio_backend.helper.request.CheckoutRequest
import com.api.mulio_backend.helper.request.CartRequest
import com.api.mulio_backend.helper.response.CartResponse
import com.api.mulio_backend.model.Order

interface CartService {
    fun addToCart(cartId: String, productId: String, cartRequest: CartRequest): CartResponse
    fun getCartByUserId(userId: String): CartResponse
    fun updateProductInCart(cartId: String, productId: String, cartRequest: CartRequest): CartResponse
    fun deleteProductFromCart(cartId: String, productId: String): CartResponse
    fun checkout(cartId: String, checkoutRequest: CheckoutRequest): Order
}