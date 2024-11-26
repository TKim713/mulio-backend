package com.api.mulio_backend.controller

import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.ChangePasswordRequest
import com.api.mulio_backend.helper.request.CustomerRequest
import com.api.mulio_backend.helper.response.*
import com.api.mulio_backend.model.Product
import com.api.mulio_backend.service.*
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
    private val productService: ProductService,
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

    @GetMapping("/cart")
    fun getCartByUserId(@RequestHeader("Authorization") token: String): ResponseEntity<ResponseObject<CartResponse>> {
        val jwtToken = token.replace("Bearer ", "")
        return try {
            val cartResponse = cartService.getCart(jwtToken)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "Cart retrieved successfully", cartResponse))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), "${e.message}", null))
        }
    }

    @GetMapping("/orders")
    fun getOrderByUserId(
        @RequestHeader("Authorization") token: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ResponseObject<Page<OrderResponse>>> {
        val jwtToken = token.replace("Bearer ", "")
        return try {
            val orders = orderService.getOrder(jwtToken, page, size)
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

    @PutMapping("/update-info")
    fun updateUserInfo(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody customerRequest: CustomerRequest
    ): ResponseEntity<ResponseObject<CustomerResponse>> {
        val jwtToken = token.replace("Bearer ", "")
        return try {
            val updatedCustomerResponse = customerService.updateCustomerInfo(jwtToken, customerRequest)
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

    @PostMapping("/change-password")
    fun changePassword(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody changePasswordRequest: ChangePasswordRequest
    ): ResponseEntity<ResponseObject<String>> {
        val jwtToken = token.replace("Bearer ", "")
        return try {
            val success = userService.changePassword(jwtToken, changePasswordRequest)
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

    @GetMapping("/customer-info")
    fun getCustomerInfo(@RequestHeader("Authorization") token: String): ResponseEntity<ResponseObject<CustomerResponse>> {
        val jwtToken = token.replace("Bearer ", "")

        return try {
            val response = customerService.getCustomerInfo(jwtToken)

            ResponseEntity.ok(ResponseObject(HttpStatus.OK.value(), "Customer info retrieved successfully", response))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), e.message ?: "Error retrieving customer info", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "An unexpected error occurred: ${e.message}", null))
        }
    }

    @PostMapping("/wishlist")
    fun addToWishlist(
        @RequestHeader("Authorization") token: String,
        @RequestParam skuBase: String
    ): ResponseEntity<ResponseMessage<List<ProductResponse>>> {
        val jwtToken = token.replace("Bearer ", "")
        return try {
            val response = productService.addToWishlistBySkuBase(jwtToken, skuBase)
            ResponseEntity.ok(ResponseMessage("Products added to wishlist successfully", response))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseMessage("Error adding products to wishlist: ${e.message}", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessage("An unexpected error occurred: ${e.message}", null))
        }
    }

    @GetMapping("/wishlist")
    fun getWishlist(@RequestHeader("Authorization") token: String): ResponseEntity<ResponseMessage<List<ProductResponse>>> {
        val jwtToken = token.replace("Bearer ", "")
        return try {
            val wishlist = productService.getWishlist(jwtToken)
            ResponseEntity.ok(ResponseMessage("Wishlist retrieved successfully", wishlist))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseMessage("Error retrieving wishlist: ${e.message}", emptyList()))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessage("An unexpected error occurred: ${e.message}", emptyList()))
        }
    }

    @DeleteMapping("/wishlist")
    fun deleteFromWishlist(
        @RequestHeader("Authorization") token: String,
        @RequestParam skuBase: String
    ): ResponseEntity<ResponseMessage<List<ProductResponse>>> {
        val jwtToken = token.replace("Bearer ", "")
        return try {
            val response = productService.deleteFromWishlistBySkuBase(jwtToken, skuBase)
            ResponseEntity.ok(ResponseMessage("Products removed from wishlist successfully", response))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseMessage("Error removing products from wishlist: ${e.message}", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseMessage("An unexpected error occurred: ${e.message}", null))
        }
    }
}