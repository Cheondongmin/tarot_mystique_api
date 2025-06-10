package com.hangtudy.app.infrastructure.auth

import com.hangtudy.app.domain.auth.TokenProvider
import com.hangtudy.app.domain.user.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret:your-256-bit-secret-key-for-jwt-token-generation}")
    private val secret: String,
    
    @Value("\${jwt.expiration:86400000}")
    private val expirationTime: Long // 24시간
) : TokenProvider {
    
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())
    
    override fun generateToken(user: User): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationTime)
        
        return Jwts.builder()
            .setSubject(user.id)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .claim("email", user.email)
            .claim("name", user.name)
            .claim("role", user.role.name)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }
    
    override fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: Exception) {
            false
        }
    }
    
    override fun getUserIdFromToken(token: String): String {
        val claims = getClaims(token)
        return claims.subject
    }
    
    override fun getRoleFromToken(token: String): String {
        val claims = getClaims(token)
        return claims["role"] as String
    }
    
    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
