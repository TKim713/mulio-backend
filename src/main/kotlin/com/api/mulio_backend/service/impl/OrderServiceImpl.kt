package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.MapData
import com.api.mulio_backend.helper.response.OrderResponse
import com.api.mulio_backend.repository.OrderRepository
import com.api.mulio_backend.service.CartService
import com.api.mulio_backend.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderServiceImpl @Autowired constructor(
    private val orderRepository: OrderRepository,
    private val cartService: CartService,
    private val mapData: MapData
) : OrderService {

    private val now: Date = Date()

    override fun getOrderByUserId(userId: String, page: Int, size: Int): Page<OrderResponse> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"))
        val ordersPage = orderRepository.findByUserId(userId, pageable)

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
    }
}