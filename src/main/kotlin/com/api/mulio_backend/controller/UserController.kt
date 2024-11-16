package com.api.mulio_backend.controller

import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.response.CartResponse
import com.api.mulio_backend.helper.response.ResponseObject
import com.api.mulio_backend.service.CartService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController @Autowired constructor (
    private val cartService: CartService
) {

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

}