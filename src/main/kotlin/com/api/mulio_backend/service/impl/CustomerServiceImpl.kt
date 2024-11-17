package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.MapData
import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CustomerRequest
import com.api.mulio_backend.helper.response.CustomerResponse
import com.api.mulio_backend.model.Customer
import com.api.mulio_backend.repository.CustomerRepository
import com.api.mulio_backend.service.CustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerServiceImpl @Autowired constructor(
    private val customerRepository: CustomerRepository,
    private val mapData: MapData
) : CustomerService{
    private val now: Date = Date()

    override fun updateCustomerInfoByUserId(userId: String, customerRequest: CustomerRequest): CustomerResponse {
        var existingCustomer = customerRepository.findByUserId(userId).orElse(null)

        if (existingCustomer == null) {
            existingCustomer = Customer(
                customerId = UUID.randomUUID().toString(),
                userId = userId,
                fullName = customerRequest.fullName,
                phone = customerRequest.phone,
                address = customerRequest.address,
                createdAt = now,
                updatedAt = now
            )
        } else {
            existingCustomer.fullName = customerRequest.fullName
            existingCustomer.phone = customerRequest.phone
            existingCustomer.address = customerRequest.address
            existingCustomer.updatedAt = now
        }

        val savedCustomer = customerRepository.save(existingCustomer)
        val response = mapData.mapOne(savedCustomer, CustomerResponse::class.java)

        return response
    }
}