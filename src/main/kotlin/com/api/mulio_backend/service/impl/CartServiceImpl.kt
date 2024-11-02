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

    // Hàm thêm sản phẩm vào giỏ hàng
    override fun addToCart(addProductToCartRequest: AddProductToCartRequest): CartResponse {
        // Find the product by name and color
        val product = productRepository.findByProductNameAndColor(addProductToCartRequest.productName, addProductToCartRequest.color)
            ?: throw CustomException("Product not found", HttpStatus.NOT_FOUND)

        // Get the user's cart
        val existingCart = cartRepository.findByUserId(addProductToCartRequest.userId)

        // New or existing cart
        val updatedProducts = existingCart?.products?.toMutableList() ?: mutableListOf()

        // Check if the product already exists in the cart
        val existingItem = updatedProducts.find { it.productId == product.productId }
        if (existingItem != null) {
            // If exists, update the quantity
            existingItem.amount += addProductToCartRequest.amount
        } else {
            // If not, add a new product to the list
            if (existingCart != null) {
                updatedProducts.add(
                    CartProduct(
                        cartProductId = UUID.randomUUID().toString(),
                        cartId = existingCart.cartId,
                        productId = product.productId,
                        amount = addProductToCartRequest.amount,
                        price = product.price, // Get the price from the product
                        createdAt = Date(),
                    )
                )
            }
        }

        // Update total quantity and total price
        val totalNumber = updatedProducts.sumOf { it.amount }
        val totalPrice = updatedProducts.fold(0f) { acc, item ->
            acc + (item.price * item.amount)
        }

        // Update the cart
        val updatedCart = existingCart?.copy(
            products = updatedProducts,
            totalNumber = totalNumber,
            totalPrice = totalPrice,
            updatedAt = Date()
        )

        // Save the updated cart
        val savedCart = updatedCart?.let { cartRepository.save(it) }

        // Create List<CartProductResponse> from List<CartProduct>
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

    // Hàm lấy tất cả sản phẩm trong giỏ hàng
    override fun getCartByUserId(userId: String): CartResponse {
        val existingCart = cartRepository.findByUserId(userId)
            ?: throw CustomException("User cart not found", HttpStatus.NOT_FOUND)
        return mapData.mapOne(existingCart, CartResponse::class.java)
    }

    override fun checkout(userId: String, checkoutRequest: CheckoutRequest): Order {
        val existingCart = cartRepository.findByUserId(userId)
            ?: throw CustomException("User cart not found", HttpStatus.NOT_FOUND)

        // Lấy sản phẩm thanh toán
        val checkoutProducts = existingCart.products.filter { cartProduct ->
            checkoutRequest.itemsToCheckout.any { it.productId == cartProduct.productId }
        }

        // Tính tổng tiền
        val totalPrice = checkoutProducts.fold(0f) { acc, item ->
            acc + (item.price * item.amount)
        }

//        if (checkoutRequest.totalPrice != totalPrice) {
//            throw CustomException("Total price mismatch", HttpStatus.BAD_REQUEST)
//        }

        // Tạo order
        val order = createOrderFromCart(userId, checkoutProducts, totalPrice)

        // Xóa sản phẩm thanh toán
        val remainingProducts = existingCart.products.filterNot { cartProduct ->
            checkoutRequest.itemsToCheckout.any { it.productId == cartProduct.productId }
        }

        // Cập nhật lại giỏ hàng
        existingCart.products = remainingProducts
        existingCart.totalNumber = remainingProducts.sumOf { it.amount }
        existingCart.totalPrice = remainingProducts.fold(0f) { acc, item -> acc + (item.price * item.amount) }
        existingCart.updatedAt = Date()
        cartRepository.save(existingCart)

        return order
    }

    private fun createOrderFromCart(userId: String, checkoutProducts: List<CartProduct>, totalPrice: Float): Order {
        val orderProducts = checkoutProducts.map { cartProduct ->
            OrderProduct(
                orderProductId = UUID.randomUUID().toString(),
                orderId = "",
                productId = cartProduct.productId,
                price = cartProduct.price,
                amount = cartProduct.amount,
                createdAt = Date()
            )
        }

        val order = Order(
            orderId = UUID.randomUUID().toString(),
            userId = userId,
            totalPrice = totalPrice,
            orderDate = Date(),
            orderProduct = orderProducts,
            createdAt = Date()
        )

        return orderRepository.save(order)
    }
}