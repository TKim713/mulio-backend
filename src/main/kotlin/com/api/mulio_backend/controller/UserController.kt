package com.api.mulio_backend.controller

import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.ChangePasswordRequest
import com.api.mulio_backend.helper.request.CustomerRequest
import com.api.mulio_backend.helper.response.*
import com.api.mulio_backend.service.CartService
import com.api.mulio_backend.service.CustomerService
import com.api.mulio_backend.service.OrderService
import com.api.mulio_backend.service.UserService
import jakarta.validation.Valid
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
    private val orderService: OrderService,
    private val customerService: CustomerService
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

    @PutMapping("/{userId}/update-info")
    fun updateUserInfo(
        @PathVariable userId: String,
        @Valid @RequestBody customerRequest: CustomerRequest
    ): ResponseEntity<ResponseObject<CustomerResponse>> {
        return try {
            val updatedCustomerResponse = customerService.updateCustomerInfoByUserId(userId, customerRequest)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "Customer information updated successfully", updatedCustomerResponse))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), e.message ?: "Error updating customer information", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Unexpected error: ${e.message}", null))
        }
    }

    @PostMapping("/{userId}/change-password")
    fun changePassword(
        @PathVariable userId: String,
        @Valid @RequestBody changePasswordRequest: ChangePasswordRequest
    ): ResponseEntity<ResponseObject<String>> {
        return try {
            val success = userService.changePassword(userId, changePasswordRequest)
            if (success) {
                ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseObject(HttpStatus.OK.value(), "Password changed successfully", "Success"))
            } else {
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Failed to change password", null))
            }
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), e.message ?: "Error changing password", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error: ${e.message}", null))
        }
    }
}