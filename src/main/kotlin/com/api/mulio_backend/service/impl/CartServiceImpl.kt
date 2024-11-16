package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.MapData
import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CheckoutRequest
import com.api.mulio_backend.helper.request.AddProductToCartRequest
import com.api.mulio_backend.helper.response.CartProductResponse
import com.api.mulio_backend.helper.response.CartResponse
import com.api.mulio_backend.model.CartProduct
import com.api.mulio_backend.model.Order
import com.api.mulio_backend.model.OrderProduct
import com.api.mulio_backend.repository.CartRepository
import com.api.mulio_backend.repository.OrderRepository
import com.api.mulio_backend.repository.ProductRepository
import com.api.mulio_backend.service.CartService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class CartServiceImpl @Autowired constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val mapData: MapData
) : CartService {

    private val now: Date = Date()

    override fun addToCart(cartId: String, addProductToCartRequest: AddProductToCartRequest): CartResponse {
        val existingCart = cartRepository.findById(cartId).orElseThrow {
            CustomException("Cart not found", HttpStatus.NOT_FOUND)
        }

        val product = productRepository.findByProductNameAndColorAndSize(
            addProductToCartRequest.productName,
            addProductToCartRequest.color,
            addProductToCartRequest.size
        ) ?: throw CustomException("Product not found", HttpStatus.NOT_FOUND)

        val updatedProducts = existingCart?.products?.toMutableList() ?: mutableListOf()

        val existingItem = updatedProducts.find { it.productId == product.productId.toString() }
        if (existingItem != null) {
            existingItem.amount += addProductToCartRequest.amount
        } else {
            if (existingCart != null) {
                updatedProducts.add(
                    CartProduct(
                        productId = product.productId.toString(),
                        amount = addProductToCartRequest.amount,
                        price = product.price
                    )
                )
            }
        }

        val totalNumber = updatedProducts.sumOf { it.amount }
        val totalPrice = updatedProducts.fold(0f) { acc, item ->
            acc + (item.price * item.amount)
        }

        val updatedCart = existingCart?.copy(
            products = updatedProducts,
            totalNumber = totalNumber,
            totalPrice = totalPrice,
            updatedAt = Date()
        )

        val savedCart = updatedCart?.let { cartRepository.save(it) }

        val cartProductResponses = savedCart?.products?.map { cartProduct ->
            val productDetails = productRepository.findById(cartProduct.productId).orElse(null)
            CartProductResponse(
                productName = productDetails?.productName,
                price = productDetails?.price,
                description = productDetails?.description,
                size = productDetails?.size,
                color = productDetails?.color,
                amount = cartProduct.amount,
                productType = productDetails?.productType,
                image = productDetails?.image
            )
        }

        val cartResponse = mapData.mapOne(savedCart, CartResponse::class.java)
        if (cartProductResponses != null) {
            cartResponse.products = cartProductResponses
        }

        return cartResponse
    }

    override fun getCartByUserId(userId: String): CartResponse {
        val existingCart = cartRepository.findByUserId(userId)
            ?: throw CustomException("User cart not found", HttpStatus.NOT_FOUND)
        return mapData.mapOne(existingCart, CartResponse::class.java)
    }

    override fun checkout(cartId: String, checkoutRequest: CheckoutRequest): Order {
        val existingCart = cartRepository.findById(cartId).orElseThrow {
            CustomException("Cart not found", HttpStatus.NOT_FOUND)
        }

        val checkoutProducts = mutableListOf<CartProduct>()
        existingCart.products.forEach { cartProduct ->
            val itemToCheckout = checkoutRequest.itemsToCheckout.find { it.productId == cartProduct.productId }
            if (itemToCheckout != null) {
                if (itemToCheckout.amount > cartProduct.amount) {
                    throw CustomException(
                        "Requested amount for product ${cartProduct.productId} exceeds available quantity",
                        HttpStatus.BAD_REQUEST
                    )
                }
                cartProduct.amount -= itemToCheckout.amount

                checkoutProducts.add(
                    CartProduct(
                        productId = cartProduct.productId,
                        price = cartProduct.price,
                        amount = itemToCheckout.amount
                    )
                )

                val product = productRepository.findById(cartProduct.productId).orElseThrow {
                    CustomException("Product not found", HttpStatus.NOT_FOUND)
                }

                if (product.amount < itemToCheckout.amount) {
                    throw CustomException(
                        "Not enough stock for product ${product.productId}",
                        HttpStatus.BAD_REQUEST
                    )
                }

                product.amount -= itemToCheckout.amount
                productRepository.save(product)
            }
        }

        val remainingProducts = existingCart.products.filter { it.amount > 0 }
        existingCart.products = remainingProducts

        val totalPrice = checkoutProducts.fold(0f) { acc, item ->
            acc + (item.price * item.amount)
        }

        if (checkoutRequest.totalPrice != totalPrice) {
            throw CustomException("Total price mismatch", HttpStatus.BAD_REQUEST)
        }

        val order = Order(
            orderId = UUID.randomUUID().toString(),
            userId = existingCart.userId,
            totalPrice = totalPrice,
            orderDate = Date(),
            orderProduct = checkoutProducts,
            createdAt = Date()
        )

        orderRepository.save(order)

        existingCart.totalNumber = remainingProducts.sumOf { it.amount }
        existingCart.totalPrice = remainingProducts.fold(0f) { acc, item -> acc + (item.price * item.amount) }
        existingCart.updatedAt = Date()
        cartRepository.save(existingCart)

        return order
    }
}