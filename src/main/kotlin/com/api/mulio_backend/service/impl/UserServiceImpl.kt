package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.MapData
import com.api.mulio_backend.helper.enums.Role
import com.api.mulio_backend.helper.enums.TokenType
import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CreateUserRequest
import com.api.mulio_backend.helper.response.CartResponse
import com.api.mulio_backend.helper.response.CreateUserResponse
import com.api.mulio_backend.model.Cart
import com.api.mulio_backend.model.Token
import com.api.mulio_backend.model.User
import com.api.mulio_backend.repository.CartRepository
import com.api.mulio_backend.repository.TokenRepository
import com.api.mulio_backend.repository.UserRepository
import com.api.mulio_backend.service.EmailService
import com.api.mulio_backend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val cartRepository: CartRepository,
    private val emailService: EmailService,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val mapData: MapData
) : UserService {

    private val now: Date = Date()

    override fun createUser(userRequest: CreateUserRequest): CreateUserResponse {
        // Kiểm tra email có tồn tại hay không
        val existingUser = userRepository.findByEmail(userRequest.email)
        if (existingUser != null) {
            throw CustomException("Email already in use", HttpStatus.CONFLICT)
        }

        // Mã hóa mật khẩu
        val password = passwordEncoder.encode(userRequest.password)

        // Tạo đối tượng User
        val user = User(
            userId = UUID.randomUUID().toString(),
            username = userRequest.username,
            email = userRequest.email,
            password = password,
            role = Role.valueOf(userRequest.role),
            createdAt = now
        )

        val savedUser = userRepository.save(user)

        // Tạo token xác thực
        val tokenStr = UUID.randomUUID().toString()
        val token = Token(
            tokenId = UUID.randomUUID().toString(),
            token = tokenStr,
            tokenType = TokenType.BEARER,
            expired = false,
            revoked = false,
            userId = savedUser.userId,
            createdAt = now
        )
        val savedToken = tokenRepository.save(token)

        // Gửi email xác thực với tên người dùng
        emailService.sendEmail(savedUser.email, "Xác Thực Email của Bạn cho Mulio!", tokenStr, savedUser.username)

        val response = mapData.mapOne(savedUser, CreateUserResponse::class.java)
        response.token = savedToken.token
        return response
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

                val newCart = Cart(
                        cartId = UUID.randomUUID().toString(),
                        userId = user.userId,
                        products = emptyList(),
                        totalNumber = 0,
                        totalPrice = 0f,
                        createdAt = now
                    )
                val savedCart = newCart.let { cartRepository.save(it) }
                mapData.mapOne(savedCart, CartResponse::class.java)

                "Xác thực email thành công!"
            } else {
                "Người dùng không tìm thấy!"
            }
        } else {
            "Liên kết xác minh không hợp lệ hoặc đã hết hạn!"
        }
    }
}