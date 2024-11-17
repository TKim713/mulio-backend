package com.api.mulio_backend.controller

import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.response.CartResponse
import com.api.mulio_backend.helper.response.OrderResponse
import com.api.mulio_backend.helper.response.ResponseObject
import com.api.mulio_backend.helper.response.UserResponse
import com.api.mulio_backend.service.CartService
import com.api.mulio_backend.service.OrderService
import com.api.mulio_backend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController @Autowired constructor (
    private val userService: UserService,
    private val cartService: CartService,
    private val orderService: OrderService
) {

    @GetMapping
    fun getUser(@RequestHeader("Authorization") token: String): ResponseEntity<ResponseObject<UserResponse>> {
        val jwtToken = token.replace("Bearer ", "")

        return try {
            val userResponse = userService.getUser(jwtToken)

            ResponseEntity.ok(ResponseObject(HttpStatus.OK.value(), "User details retrieved successfully", userResponse))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), e.message ?: "Error retrieving user details", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "An unexpected error occurred: ${e.message}", null))
        }
    }

    @GetMapping("{userId}/cart")
    fun getCartByUserId(@PathVariable userId: String): ResponseEntity<ResponseObject<CartResponse>> {
        return try {
            val cartResponse = cartService.getCartByUserId(userId)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "Cart retrieved successfully", cartResponse))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), "${e.message}", null))
        }
    }

    @GetMapping("{userId}/orders")
    fun getOrderByUserId(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ResponseObject<Page<OrderResponse>>> {
        return try {
            val orders = orderService.getOrderByUserId(userId, page, size)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "Orders retrieved successfully", orders))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), "${e.message}", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Error retrieving orders: ${e.message}", null))
        }
    }
}