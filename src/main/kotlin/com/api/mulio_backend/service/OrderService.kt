package com.api.mulio_backend.service

import com.api.mulio_backend.helper.response.OrderResponse
import org.springframework.data.domain.Page

interface OrderService {
    fun getOrder(tokenStr: String, page: Int, size: Int): Page<OrderResponse>
}