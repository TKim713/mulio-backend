package com.api.mulio_backend.controller

import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CartRequest
import com.api.mulio_backend.helper.request.CheckoutRequest
import com.api.mulio_backend.helper.response.CartResponse
import com.api.mulio_backend.helper.response.ResponseObject
import com.api.mulio_backend.service.CartService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
class CartController @Autowired constructor(
    private val cartService: CartService
) {

    @PostMapping("/{cartId}/products/{productId}")
    fun addToCart(
        @PathVariable cartId: String,
        @PathVariable productId: String, // Change to use productId in the URL
        @RequestBody cartRequest: CartRequest
    ): ResponseEntity<ResponseObject<CartResponse>> {
        return try {
            val cartResponse = cartService.addToCart(cartId, productId, cartRequest)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "Product added to cart successfully", cartResponse))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), "${e.message}", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Error adding product to cart: ${e.message}", null))
        }
    }

    @PutMapping("/{cartId}/products/{productId}")
    fun updateProductInCart(
        @PathVariable cartId: String,
        @PathVariable productId: String,
        @RequestBody cartRequest: CartRequest
    ): ResponseEntity<ResponseObject<CartResponse>> {
        return try {
            val cartResponse = cartService.updateProductInCart(cartId, productId, cartRequest)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "Product updated in cart successfully", cartResponse))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), "${e.message}", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Error updating product in cart: ${e.message}", null))
        }
    }

    @DeleteMapping("/{cartId}/products/{productId}")
    fun deleteProductFromCart(
        @PathVariable cartId: String,
        @PathVariable productId: String
    ): ResponseEntity<ResponseObject<CartResponse>> {
        return try {
            val cartResponse = cartService.deleteProductFromCart(cartId, productId)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "Product removed from cart successfully", cartResponse))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), "${e.message}", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Error removing product from cart: ${e.message}", null))
        }
    }

    @PostMapping("/{cartId}/checkout")
    fun checkout(@PathVariable cartId: String, @RequestBody checkoutRequest: CheckoutRequest): ResponseEntity<ResponseObject<String>> {
        return try {
            cartService.checkout(cartId, checkoutRequest)

            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "Checkout successfully", ""))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), e.message ?: "Custom error occurred", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Error during checkout: ${e.message}", null))
        }
    }
}