package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.JwtTokenUtil
import com.api.mulio_backend.config.MapData
import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.response.OrderResponse
import com.api.mulio_backend.repository.OrderRepository
import com.api.mulio_backend.repository.TokenRepository
import com.api.mulio_backend.repository.UserRepository
import com.api.mulio_backend.service.CartService
import com.api.mulio_backend.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderServiceImpl @Autowired constructor(
    private val orderRepository: OrderRepository,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
    private val jwtTokenUtil: JwtTokenUtil,
    private val cartService: CartService,
    private val mapData: MapData
) : OrderService {

    private val now: Date = Date()

    override fun getOrder(tokenStr: String, page: Int, size: Int): Page<OrderResponse> {
        val token = tokenRepository.findByAccessToken(tokenStr)

        if (token != null) {
            val email = jwtTokenUtil.getUsernameFromToken(token.accessToken)
            val user = userRepository.findByEmail(email)

            if (user != null) {
                val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"))
                val ordersPage = orderRepository.findByUserId(user.userId, pageable)

                return ordersPage.map { order ->
                    OrderResponse(
                        orderId = order.orderId,
                        fullName = order.fullName,
                        phone = order.phone,
                        address = order.address,
                        city = order.city,
                        district = order.district,
                        ward = order.ward,
                        paymentMethod = order.paymentMethod,
                        totalPrice = order.totalPrice,
                        orderDate = order.orderDate,
                        orderProduct = cartService.mapToCartProductResponse(order.orderProduct)
                    )
                }
            } else {
                throw CustomException("User not found", HttpStatus.NOT_FOUND)
            }
        } else {
            throw CustomException("Token not found", HttpStatus.NOT_FOUND)
        }
    }
}