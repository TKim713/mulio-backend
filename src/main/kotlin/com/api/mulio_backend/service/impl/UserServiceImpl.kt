package com.api.mulio_backend.service.impl

import com.api.mulio_backend.helper.enums.Role
import com.api.mulio_backend.helper.request.CreateUserRequest
import com.api.mulio_backend.helper.response.CreateUserResponse
import com.api.mulio_backend.model.User
import com.api.mulio_backend.repository.UserRepository
import com.api.mulio_backend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) : UserService {

    override fun createUser(userRequest: CreateUserRequest): CreateUserResponse {
        val password = passwordEncoder.encode(userRequest.password)

        val user = User(
            username = userRequest.username,
            email = userRequest.email,
            password = password,
            role = Role.valueOf(userRequest.role),
            createdAt = Date()
        )

        val savedUser = userRepository.save(user)

        return CreateUserResponse(
            username = savedUser.username,
            email = savedUser.email,
            password = savedUser.password,
            role = savedUser.role.toString()
        )
    }
}