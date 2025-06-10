package com.hangtudy.app.domain.user

import com.hangtudy.app.domain.auth.PasswordEncoder
import com.hangtudy.app.domain.auth.TokenProvider
import com.hangtudy.app.domain.email.EmailSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val emailSender: EmailSender
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun register(email: String, password: String, name: String): Result<User> {
        return runCatching {
            logger.info("👤 Starting user registration for email: $email")
            
            // 이메일 중복 체크
            if (userRepository.existsByEmail(email)) {
                throw DuplicateEmailException("이미 사용중인 이메일입니다.")
            }
            
            // 비밀번호 암호화
            val encodedPassword = passwordEncoder.encode(password)
            
            // 이메일 인증 토큰 생성
            val verificationToken = UUID.randomUUID().toString()
            
            // 사용자 생성
            val user = User.create(
                email = email,
                password = encodedPassword,
                name = name,
                emailVerificationToken = verificationToken
            )
            
            val savedUser = userRepository.save(user)
            
            // 이메일 발송
            emailSender.sendVerificationEmail(email, verificationToken)
                .onFailure { e ->
                    logger.error("❌ Failed to send verification email: ${e.message}", e)
                }
            
            logger.info("✅ User registered successfully: ${savedUser.id}")
            savedUser
        }
    }

    fun login(email: String, password: String): Result<Pair<String, User>> {
        return runCatching {
            logger.info("🔐 Login attempt for email: $email")
            
            // 사용자 조회
            val user = userRepository.findByEmail(email)
                ?: throw InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.")
            
            // 비밀번호 검증
            if (!passwordEncoder.matches(password, user.password)) {
                throw InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.")
            }
            
            // 이메일 인증 확인
            if (!user.isEmailVerified) {
                throw EmailNotVerifiedException("이메일 인증이 필요합니다.")
            }
            
            // JWT 토큰 생성
            val token = tokenProvider.generateToken(user)
            
            logger.info("✅ Login successful for user: ${user.id}")
            Pair(token, user)
        }
    }

    fun getUserById(id: String): Result<User> {
        return runCatching {
            userRepository.findById(id)
                ?: throw UserNotFoundException("사용자를 찾을 수 없습니다.")
        }
    }

    fun verifyEmail(token: String, email: String): Result<User> {
        return runCatching {
            logger.info("📧 Email verification for $email with token $token")

            val user = userRepository.findByEmail(email)
                ?: throw UserNotFoundException("사용자를 찾을 수 없습니다.")

            if (user.emailVerificationToken != token) {
                throw EmailNotVerifiedException("토큰이 유효하지 않습니다.")
            }

            // 검증 완료 플래그 갱신
            val updateUser = user.copy(
                emailVerificationToken = null,
                isEmailVerified = true,
                updatedAt = LocalDateTime.now(),
                emailVerificationExpiresAt = null
            )
            userRepository.save(updateUser)
        }
    }
}

// Exceptions
class DuplicateEmailException(message: String) : RuntimeException(message)
class InvalidCredentialsException(message: String) : RuntimeException(message)
class EmailNotVerifiedException(message: String) : RuntimeException(message)
class UserNotFoundException(message: String) : RuntimeException(message)
