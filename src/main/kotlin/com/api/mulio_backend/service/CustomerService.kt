package com.api.mulio_backend.service

import com.api.mulio_backend.helper.request.CustomerRequest
import com.api.mulio_backend.helper.response.CustomerResponse

interface CustomerService {
    fun updateCustomerInfoByUserId(userId: String, customerRequest: CustomerRequest): CustomerResponse
}