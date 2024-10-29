package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.MapData
import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CreateCartRequest
import com.api.mulio_backend.helper.response.CartProductResponse
import com.api.mulio_backend.helper.response.CartResponse
import com.api.mulio_backend.model.CartProduct
import com.api.mulio_backend.repository.CartRepository
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
    private val mapData: MapData
) : CartService {

    private val now: Date = Date()

    // Hàm thêm sản phẩm vào giỏ hàng
    override fun addToCart(createCartRequest: CreateCartRequest): CartResponse {
        // Kiểm tra sản phẩm có tồn tại không
        val product = productRepository.findById(createCartRequest.productId).orElseThrow {
            CustomException("Product not found", HttpStatus.NOT_FOUND)
        }

        // Lấy giỏ hàng của người dùng
        val existingCart = cartRepository.findByUserId(createCartRequest.userId)

        // Giỏ hàng mới hoặc đã tồn tại
        val updatedProducts = existingCart?.products?.toMutableList() ?: mutableListOf()

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
        val existingItem = updatedProducts.find { it.productId == createCartRequest.productId }
        if (existingItem != null) {
            // Nếu đã có, cập nhật số lượng sản phẩm
            existingItem.amount += createCartRequest.amount
        } else {
            // Nếu chưa có, thêm sản phẩm mới vào danh sách
            updatedProducts.add(
                CartProduct(
                    cartProductId = UUID.randomUUID().toString(),
                    userId = createCartRequest.userId,
                    productId = createCartRequest.productId,
                    amount = createCartRequest.amount
                )
            )
        }

        // Cập nhật lại tổng số lượng và tổng giá
        val totalNumber = updatedProducts.sumOf { it.amount }
        val totalPrice = updatedProducts.fold(0f) { acc, item ->
            acc + (product.price * item.amount)
        }

        // Cập nhật giỏ hàng
        val updatedCart = existingCart?.copy(
            products = updatedProducts,
            totalNumber = totalNumber,
            totalPrice = totalPrice,
            updatedAt = now
        )

        // Lưu giỏ hàng đã cập nhật
        val savedCart = updatedCart?.let { cartRepository.save(it) }

        // Tạo List<CartProductResponse> từ List<CartProduct>
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
}