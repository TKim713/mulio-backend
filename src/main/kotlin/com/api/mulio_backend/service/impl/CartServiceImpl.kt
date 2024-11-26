package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.JwtTokenUtil
import com.api.mulio_backend.config.MapData
import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CheckoutRequest
import com.api.mulio_backend.helper.request.CartRequest
import com.api.mulio_backend.helper.response.CartProductResponse
import com.api.mulio_backend.helper.response.CartResponse
import com.api.mulio_backend.helper.response.OrderResponse
import com.api.mulio_backend.model.CartProduct
import com.api.mulio_backend.model.Order
import com.api.mulio_backend.repository.*
import com.api.mulio_backend.service.CartService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class CartServiceImpl @Autowired constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val cartRepository: CartRepository,
    private val jwtTokenUtil: JwtTokenUtil,
    private val orderRepository: OrderRepository,
    private val mapData: MapData
) : CartService {

    private val now: Date = Date()

    override fun addToCart(cartId: String, productId: String, cartRequest: CartRequest): CartResponse {
        val existingCart = cartRepository.findById(cartId).orElseThrow {
            CustomException("Cart not found", HttpStatus.NOT_FOUND)
        }

        val product = productRepository.findById(productId).orElseThrow {
            CustomException("Product not found", HttpStatus.NOT_FOUND)
        }

        val updatedProducts = existingCart.products.toMutableList()

        val existingItem = updatedProducts.find { it.productId == productId }
        val totalRequestedAmount = if (existingItem != null) {
            existingItem.totalAmount + cartRequest.amount
        } else {
            cartRequest.amount
        }

        // Check if the requested amount exceeds the stock
        if (totalRequestedAmount > product.amount!!) {
            throw CustomException(
                "Requested amount exceeds available stock for product: ${product.productName}. Available stock: ${product.amount}",
                HttpStatus.BAD_REQUEST
            )
        }

        if (existingItem != null) {
            existingItem.totalAmount += cartRequest.amount
            existingItem.totalPrice = existingItem.totalAmount * product.price
        } else {
            updatedProducts.add(
                CartProduct(
                    productId = productId,
                    totalAmount = cartRequest.amount,
                    totalPrice = cartRequest.amount * product.price
                )
            )
        }

        val totalNumber = updatedProducts.sumOf { it.totalAmount }
        val totalPrice = updatedProducts.fold(0f) { acc, item -> acc + item.totalPrice }

        val updatedCart = existingCart.copy(
            products = updatedProducts,
            totalNumber = totalNumber,
            totalPrice = totalPrice,
            updatedAt = now
        )

        val savedCart = cartRepository.save(updatedCart)

        val cartProductResponses = mapToCartProductResponse(savedCart.products)

        val response = mapData.mapOne(savedCart, CartResponse::class.java)
        response.products = cartProductResponses

        return response
    }

    override fun getCart(tokenStr: String): CartResponse {
        val token = tokenRepository.findByAccessToken(tokenStr)

        if (token != null) {
            val email = jwtTokenUtil.getUsernameFromToken(token.accessToken)
            val user = userRepository.findByEmail(email)

            if (user != null) {
                val existingCart = cartRepository.findByUserId(user.userId)
                    ?: throw CustomException("Cart not found for user: $user.userId", HttpStatus.NOT_FOUND)

                val productResponses = mapToCartProductResponse(existingCart.products)

                val response = mapData.mapOne(existingCart, CartResponse::class.java)
                response.products = productResponses
                return response
            } else {
                throw CustomException("User not found", HttpStatus.NOT_FOUND)
            }
        } else {
            throw CustomException("Token not found", HttpStatus.NOT_FOUND)
        }
    }

    override fun updateProductInCart(cartId: String, productId: String, cartRequest: CartRequest): CartResponse {
        val existingCart = cartRepository.findById(cartId).orElseThrow {
            CustomException("Cart not found", HttpStatus.NOT_FOUND)
        }

        val product = productRepository.findById(productId).orElseThrow {
            CustomException("Product not found", HttpStatus.NOT_FOUND)
        }

        val updatedProducts = existingCart.products.toMutableList()

        val existingItem = updatedProducts.find { it.productId == productId }
        if (existingItem != null) {
            if (cartRequest.amount > product.amount!!) {
                throw CustomException(
                    "Requested amount exceeds available stock for product: ${product.productName}. Available stock: ${product.amount}",
                    HttpStatus.BAD_REQUEST
                )
            }

            if (cartRequest.amount > 0) {
                existingItem.totalAmount = cartRequest.amount
                existingItem.totalPrice = existingItem.totalAmount * product.price
            } else {
                updatedProducts.remove(existingItem)
            }
        } else {
            throw CustomException("Product not found in the cart", HttpStatus.NOT_FOUND)
        }

        val totalNumber = updatedProducts.sumOf { it.totalAmount }
        val totalPrice = updatedProducts.fold(0f) { acc, item -> acc + item.totalPrice }

        val updatedCart = existingCart.copy(
            products = updatedProducts,
            totalNumber = totalNumber,
            totalPrice = totalPrice,
            updatedAt = now
        )

        val savedCart = cartRepository.save(updatedCart)

        val cartProductResponses = mapToCartProductResponse(savedCart.products)

        val response = mapData.mapOne(savedCart, CartResponse::class.java)
        response.products = cartProductResponses

        return response
    }

    override fun deleteProductFromCart(cartId: String, productId: String): CartResponse {
        val existingCart = cartRepository.findById(cartId).orElseThrow {
            CustomException("Cart not found", HttpStatus.NOT_FOUND)
        }

        val updatedProducts = existingCart.products.toMutableList()

        val existingItem = updatedProducts.find { it.productId == productId }
        if (existingItem != null) {
            updatedProducts.remove(existingItem)
        } else {
            throw CustomException("Product not found in the cart", HttpStatus.NOT_FOUND)
        }

        val totalNumber = updatedProducts.sumOf { it.totalAmount }
        val totalPrice = updatedProducts.fold(0f) { acc, item -> acc + item.totalPrice }

        val updatedCart = existingCart.copy(
            products = updatedProducts,
            totalNumber = totalNumber,
            totalPrice = totalPrice,
            updatedAt = now
        )

        val savedCart = cartRepository.save(updatedCart)

        val cartProductResponses = mapToCartProductResponse(savedCart.products)

        val response = mapData.mapOne(savedCart, CartResponse::class.java)
        response.products = cartProductResponses

        return response
    }

    override fun checkout(cartId: String, checkoutRequest: CheckoutRequest): OrderResponse {
        val existingCart = cartRepository.findById(cartId).orElseThrow {
            CustomException("Cart not found", HttpStatus.NOT_FOUND)
        }

        if (existingCart.products.isEmpty()) {
            throw CustomException("No items in the cart to checkout", HttpStatus.BAD_REQUEST)
        }

        val checkoutProducts = mutableListOf<CartProduct>()

        existingCart.products.forEach { cartProduct ->
            val product = productRepository.findById(cartProduct.productId).orElseThrow {
                CustomException("Product not found", HttpStatus.NOT_FOUND)
            }

            if (product.amount!! < cartProduct.totalAmount) {
                throw CustomException(
                    "Not enough stock for product ${product.productId}",
                    HttpStatus.BAD_REQUEST
                )
            }

            checkoutProducts.add(
                CartProduct(
                    productId = cartProduct.productId,
                    totalPrice = cartProduct.totalPrice,
                    totalAmount = cartProduct.totalAmount
                )
            )
        }

        val order = Order(
            orderId = UUID.randomUUID().toString(),
            userId = existingCart.userId,
            fullName = checkoutRequest.fullName,
            phone = checkoutRequest.phone,
            address = checkoutRequest.address,
            city = checkoutRequest.city,
            district = checkoutRequest.district,
            ward = checkoutRequest.ward,
            paymentMethod = checkoutRequest.paymentMethod,
            totalPrice = existingCart.totalPrice,
            orderDate = now,
            orderProduct = checkoutProducts,
            createdAt = now
        )

        val savedOrder = orderRepository.save(order)

        existingCart.products.forEach { cartProduct ->
            val product = productRepository.findById(cartProduct.productId).orElseThrow {
                CustomException("Product not found", HttpStatus.NOT_FOUND)
            }

            product.amount = product.amount?.minus(cartProduct.totalAmount)
            productRepository.save(product)
        }

        existingCart.products = emptyList()
        existingCart.totalNumber = 0
        existingCart.totalPrice = 0f
        existingCart.updatedAt = Date()

        cartRepository.save(existingCart)

        val response = mapData.mapOne(savedOrder, OrderResponse::class.java)
        response.orderProduct = mapToCartProductResponse(savedOrder.orderProduct)

        return response
    }

    override fun mapToCartProductResponse(cartProducts: List<CartProduct>): List<CartProductResponse> {
        return cartProducts.map { cartProduct ->
            val productDetails = productRepository.findById(cartProduct.productId).orElse(null)
            CartProductResponse(
                productId = productDetails.productId.toString(),
                skuBase = productDetails.skuBase,
                skuCode = productDetails.skuCode,
                productName = productDetails.productName,
                price = productDetails.price,
                description = productDetails?.description,
                size = productDetails.size,
                color = productDetails.color,
                amount = cartProduct.totalAmount,
                limit = productDetails.amount,
                productType = productDetails.productType,
                image = productDetails.images,
                totalPrice = cartProduct.totalPrice
            )
        }
    }
}