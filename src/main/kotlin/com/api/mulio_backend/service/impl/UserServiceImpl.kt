package com.api.mulio_backend.service.impl

import com.api.mulio_backend.config.JwtRequestFilter
import com.api.mulio_backend.config.JwtTokenUtil
import com.api.mulio_backend.config.MapData
import com.api.mulio_backend.helper.enums.Role
import com.api.mulio_backend.helper.enums.TokenType
import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.ChangePasswordRequest
import com.api.mulio_backend.helper.request.CreateUserRequest
import com.api.mulio_backend.helper.response.CartResponse
import com.api.mulio_backend.helper.response.CreateUserResponse
import com.api.mulio_backend.helper.response.UserResponse
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
    private val jwtTokenUtil: JwtTokenUtil,
    private val jwtRequestFilter: JwtRequestFilter,
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
            accessToken = tokenStr,
            expired = false,
            revoked = false,
            user = savedUser.email,
            createdAt = now
        )
        val savedToken = tokenRepository.save(token)

        // Gửi email xác thực với tên người dùng
        emailService.sendEmail(savedUser.email, "Xác Thực Email của Bạn cho Mulio!", tokenStr, savedUser.username)

        val response = mapData.mapOne(savedUser, CreateUserResponse::class.java)
        response.token = savedToken.accessToken
        return response
    }

    // Method xác thực email
    override fun verifyEmail(tokenStr: String): String {
        val token = tokenRepository.findByAccessToken(tokenStr)
        return if (token != null && !token.expired && !token.revoked) {
            val user = userRepository.findByEmail(token.user)

            return if (user != null) {
                user.enabled = true // Kích hoạt tài khoản user
                userRepository.save(user)

                token.expired = true // Đánh dấu token đã hết hạn sau khi xác thực
                token.revoked = true
                token.updatedAt = now
                token.deletedAt = now
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

    override fun getUser(tokenStr: String): UserResponse {
        val token = tokenRepository.findByAccessToken(tokenStr)

        if (token != null) {
            val email = jwtTokenUtil.getUsernameFromToken(token.accessToken)

            val user = userRepository.findByEmail(email)

            if (user != null) {
                return UserResponse(
                    userId = user.userId,
                    username = user.username,
                    email = user.email,
                    role = user.role,
                    enabled = user.enabled
                )
            } else {
                throw CustomException("User not found", HttpStatus.NOT_FOUND)
            }
        } else {
            throw CustomException("Token not found", HttpStatus.NOT_FOUND)
        }
    }

    override fun changePassword(userId: String, changePasswordRequest: ChangePasswordRequest): Boolean {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException("User not found", HttpStatus.NOT_FOUND) }

        if (!passwordEncoder.matches(changePasswordRequest.oldPassword, user.password)) {
            throw CustomException("Old password is incorrect", HttpStatus.BAD_REQUEST)
        }

        if (changePasswordRequest.oldPassword == changePasswordRequest.newPassword) {
            throw CustomException("New password cannot be the same as the old password", HttpStatus.BAD_REQUEST)
        }

        val encodedNewPassword = passwordEncoder.encode(changePasswordRequest.newPassword)

        user.password = encodedNewPassword
        userRepository.save(user)

        return true
    }
}