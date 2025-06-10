package com.hangtudy.app.infrastructure.email

import com.hangtudy.app.domain.email.EmailSender
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Component
class EmailSenderImpl(
    private val javaMailSender: JavaMailSender,
    @Value("\${spring.mail.username}")
    private val fromEmail: String,
    @Value("\${app.base-url}")
    private val baseUrl: String
) : EmailSender {
    private val logger = LoggerFactory.getLogger(EmailSenderImpl::class.java)

    override fun sendVerificationEmail(email: String, token: String): Result<Unit> {
        return runCatching {
            logger.info("📧 Preparing verification email for: $email")

            val message = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setFrom(fromEmail, "🔮 Tarot Mystique 🔮")
            helper.setTo(email)
            helper.setSubject("이메일 인증 - 🔮 Tarot Mystique 🔮")

            val verificationLink = "$baseUrl/api/v1/auth/verify?token=$token&email=$email"
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: sans-serif; line-height:1.6; color:#333; padding:20px;">
                  <div style="max-width:600px; margin:0 auto; background:#f9f9f9; border-radius:8px; padding:30px;">
                    <h1 style="text-align:center; color:#6c5ce7;">🔮 Tarot Mystique 🔮</h1>
                    <h2>이메일 인증</h2>
                    <p>안녕하세요!</p>
                    <p>Tarot Mystique에 가입해 주셔서 감사합니다.</p>
                    <p>아래 버튼을 눌러 이메일 인증을 완료해주세요:</p>
                    <div style="text-align:center; margin:20px 0;">
                      <a href="$verificationLink"
                         style="display:inline-block; padding:12px 30px; background:#6c5ce7; color:#fff; text-decoration:none; border-radius:5px;">
                        이메일 인증하기
                      </a>
                    </div>
                    <p style="color:#e74c3c; font-size:14px;">⚠️ 이 링크는 24시간 동안만 유효합니다.</p>
                    <p>만약 요청하지 않으셨다면 이 메일을 무시하세요.</p>
                    <hr/>
                    <p style="font-size:12px; color:#666; text-align:center;">
                      © 2025 Tarot Mystique All rights reserved.<br/>
                      문의: aehdals9900@gmail.com
                    </p>
                  </div>
                </body>
                </html>
            """.trimIndent()

            helper.setText(htmlContent, true)
            javaMailSender.send(message)
            logger.info("✅ Verification email sent successfully to: $email")
        }.onFailure { e ->
            logger.error("❌ Failed to send verification email to $email: ${e.message}", e)
        }
    }
}