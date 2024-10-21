package com.api.mulio_backend.service.impl

import com.api.mulio_backend.service.EmailService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(@Autowired private val javaMailSender: JavaMailSender) : EmailService {

    companion object {
        private const val BASE_URL = "http://localhost:8080/api/auth/verify?token=" // Định nghĩa URL cơ bản ở đây
    }

    private val logger: Logger = LoggerFactory.getLogger(EmailServiceImpl::class.java)

    override fun sendEmail(to: String, subject: String, token: String, username: String) {
        val body = generateEmailBody(token, username)

        try {
            val message = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(body, true)
            javaMailSender.send(message)
        } catch (e: Exception) {
            logger.error("Không thể gửi email", e)
        }
    }

    private fun generateEmailBody(token: String, username: String): String {
        return """
            <p>Kính gửi <strong>$username</strong>,</p>
            <p>Cảm ơn bạn đã tham gia gia đình <strong>Mulio</strong>! Chúng tôi rất vui mừng chào đón bạn trong hành trình thời trang đầy phong cách.</p>
            <p>Vui lòng xác nhận địa chỉ email của bạn bằng cách nhấp vào liên kết dưới đây:</p>
            <p>👉 <a href="$BASE_URL$token">Xác thực Email của Bạn</a></p>
            <p>Bước này giúp chúng tôi đảm bảo rằng bạn có thể nhận được thông tin quan trọng về bộ sưu tập mới và những ưu đãi độc quyền!</p>
            <p>Nếu bạn có bất kỳ câu hỏi nào hoặc cần hỗ trợ, vui lòng liên hệ với đội ngũ hỗ trợ của chúng tôi tại <strong>support@mulio.com</strong>.</p>
            <p>Cảm ơn bạn đã trở thành một phần của cộng đồng chúng tôi! Chúng tôi mong muốn giúp bạn khám phá những xu hướng thời trang mới nhất!</p>
            <p>Trân trọng,<br>Đội Ngũ <strong>Mulio</strong><br><a href="http://www.mulio.com">www.mulio.com</a></p>
        """.trimIndent()
    }
}
