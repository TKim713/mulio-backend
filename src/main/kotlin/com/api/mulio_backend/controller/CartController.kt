package com.api.mulio_backend.controller

import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.AddProductToCartRequest
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

    @PostMapping("/{cartId}")
    fun addToCart(
        @PathVariable cartId: String,
        @RequestBody addProductToCartRequest: AddProductToCartRequest
    ): ResponseEntity<ResponseObject<CartResponse>> {
        return try {
            // Pass the cartId to the service method
            val cartResponse = cartService.addToCart(cartId, addProductToCartRequest)
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

    @GetMapping("/{userId}")
    fun getUserCart(@PathVariable userId: String): ResponseEntity<ResponseObject<CartResponse>> {
        return try {
            val cartResponse = cartService.getCartByUserId(userId)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "User cart retrieved successfully", cartResponse))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), "${e.message}", null))
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