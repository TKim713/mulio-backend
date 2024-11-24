package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.JwtTokenUtil
import com.api.mulio_backend.config.MapData
import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CustomerRequest
import com.api.mulio_backend.helper.response.CustomerResponse
import com.api.mulio_backend.model.Customer
import com.api.mulio_backend.repository.CustomerRepository
import com.api.mulio_backend.repository.TokenRepository
import com.api.mulio_backend.repository.UserRepository
import com.api.mulio_backend.service.CustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerServiceImpl @Autowired constructor(
    private val customerRepository: CustomerRepository,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
    private val jwtTokenUtil: JwtTokenUtil,
    private val mapData: MapData
) : CustomerService{
    private val now: Date = Date()

    override fun updateCustomerInfo(tokenStr: String, customerRequest: CustomerRequest): CustomerResponse {
        val token = tokenRepository.findByAccessToken(tokenStr)

        if (token != null) {
            val email = jwtTokenUtil.getUsernameFromToken(token.accessToken)
            val user = userRepository.findByEmail(email)

            if (user != null) {
                val existingCustomer = customerRepository.findByUserId(user.userId).orElseThrow {
                    CustomException("Customer not found", HttpStatus.NOT_FOUND)
                }

                existingCustomer.fullName = customerRequest.fullName
                existingCustomer.phone = customerRequest.phone
                existingCustomer.address = customerRequest.address
                existingCustomer.updatedAt = now

                val savedCustomer = customerRepository.save(existingCustomer)
                return mapData.mapOne(savedCustomer, CustomerResponse::class.java)
            } else {
                throw CustomException("User not found", HttpStatus.NOT_FOUND)
            }
        } else {
            throw CustomException("Token not found", HttpStatus.NOT_FOUND)
        }
    }

    override fun getCustomerInfo(tokenStr: String): CustomerResponse {
        val token = tokenRepository.findByAccessToken(tokenStr)

        if (token != null) {
            val email = jwtTokenUtil.getUsernameFromToken(token.accessToken)

            val user = userRepository.findByEmail(email)

            if (user != null) {
                val customer = customerRepository.findByUserId(user.userId).orElseThrow{
                    CustomException("Customer not found", HttpStatus.NOT_FOUND)
                }

                val response = mapData.mapOne(customer, CustomerResponse::class.java)
                response.email = email
                return response
            } else {
                throw CustomException("User not found", HttpStatus.NOT_FOUND)
            }
        } else {
            throw CustomException("Token not found", HttpStatus.NOT_FOUND)
        }
    }
}