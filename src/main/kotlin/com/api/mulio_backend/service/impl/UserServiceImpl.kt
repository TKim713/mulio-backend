package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.MapData
import com.api.mulio_backend.helper.enums.Role
import com.api.mulio_backend.helper.enums.TokenType
import com.api.mulio_backend.helper.request.CreateUserRequest
import com.api.mulio_backend.helper.response.CreateUserResponse
import com.api.mulio_backend.model.Token
import com.api.mulio_backend.model.User
import com.api.mulio_backend.repository.TokenRepository
import com.api.mulio_backend.repository.UserRepository
import com.api.mulio_backend.service.EmailService
import com.api.mulio_backend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val emailService: EmailService,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val mapData: MapData
) : UserService {

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    private val now: Date = Date()

    override fun createUser(userRequest: CreateUserRequest): CreateUserResponse {
        // Mã hóa mật khẩu
        val password = passwordEncoder.encode(userRequest.password)

        // Tạo đối tượng User
        val user = User(
            username = userRequest.username,
            email = userRequest.email,
            password = password,
            role = Role.valueOf(userRequest.role),
            createdAt = now // Change to Date
        )

        val savedUser = userRepository.save(user)

        // Tạo token xác thực
        val tokenStr = UUID.randomUUID().toString()
        val token = savedUser.userId?.let {
            Token(
                token = tokenStr,
                tokenType = TokenType.BEARER,
                expired = false,
                revoked = false,
                userId = it,
                createdAt = now
            )
        }
        if (token != null) {
            tokenRepository.save(token)
        }

        // Gửi email xác thực với tên người dùng
        emailService.sendEmail(savedUser.email, "🌟 Xác Thực Email của Bạn cho Mulio! 🌟", tokenStr, savedUser.username)

        return mapData.mapOne(savedUser, CreateUserResponse::class.java)
    }

    // Method xác thực email
    override fun verifyEmail(tokenStr: String): String {
        val token = tokenRepository.findByToken(tokenStr)
        return if (token != null && !token.expired && !token.revoked) {
            val user = userRepository.findById(token.userId).orElse(null)

            return if (user != null) {
                user.enabled = true // Kích hoạt tài khoản user
                userRepository.save(user)

                token.expired = true // Đánh dấu token đã hết hạn sau khi xác thực
                tokenRepository.save(token)

                "Xác thực email thành công!"
            } else {
                "Người dùng không tìm thấy!"
            }
        } else {
            "Liên kết xác minh không hợp lệ hoặc đã hết hạn!"
        }
    }
}