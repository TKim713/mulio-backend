package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.MapData
import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CreateCartRequest
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
        val updatedItems = existingCart?.products?.toMutableList() ?: mutableListOf()

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
        val existingItem = updatedItems.find { it.productId == createCartRequest.productId }
        if (existingItem != null) {
            // Nếu đã có, cập nhật số lượng sản phẩm
            existingItem.amount += createCartRequest.amount
        } else {
            // Nếu chưa có, thêm sản phẩm mới vào danh sách
            updatedItems.add(CartProduct(cartProductId = UUID.randomUUID().toString(), productId = createCartRequest.productId, amount = createCartRequest.amount, product = product))
        }

        // Cập nhật lại tổng số lượng và tổng giá
        val totalNumber = updatedItems.sumOf { it.amount }
        val totalPrice = updatedItems.fold(0f) { acc, item ->
            acc + (item.product.price * item.amount)
        }

        // Cập nhật giỏ hàng
        val updatedCart = existingCart?.copy(
            products = updatedItems,
            totalNumber = totalNumber,
            totalPrice = totalPrice,
            updatedAt = now
        )

        // Lưu giỏ hàng đã cập nhật
        val savedCart = updatedCart?.let { cartRepository.save(it) }

        // Chuyển đổi giỏ hàng đã lưu thành phản hồi
        return mapData.mapOne(savedCart, CartResponse::class.java)
    }

    // Hàm lấy tất cả sản phẩm trong giỏ hàng
    override fun getCartByUserId(userId: String): CartResponse {
        // Lấy giỏ hàng của người dùng nếu đã có, nếu chưa tạo giỏ hàng mới
        val existingCart = cartRepository.findByUserId(userId)
        return mapData.mapOne(existingCart, CartResponse::class.java)
    }
}