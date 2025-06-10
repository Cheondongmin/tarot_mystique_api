package com.hangtudy.filter

import com.hangtudy.app.domain.auth.TokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider
) : OncePerRequestFilter() {
    
    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath
        
        // 인증이 필요없는 경로는 통과
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response)
            return
        }
        
        val token = extractToken(request)
        
        if (token != null && tokenProvider.validateToken(token)) {
            // 토큰이 유효하면 request attribute에 사용자 정보 추가
            val userId = tokenProvider.getUserIdFromToken(token)
            val role = tokenProvider.getRoleFromToken(token)
            
            request.setAttribute("userId", userId)
            request.setAttribute("userRole", role)
            
            logger.debug("Valid JWT token for user: $userId with role: $role")
        } else if (isProtectedPath(path)) {
            // 보호된 경로인데 토큰이 없거나 유효하지 않으면 401 반환
            logger.warn("Invalid or missing token for protected path: $path")
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required")
            return
        }
        
        filterChain.doFilter(request, response)
    }
    
    private fun extractToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else {
            null
        }
    }
    
    private fun isPublicPath(path: String): Boolean {
        val publicPaths = listOf(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/verify",
            "/api/v1/tarot",  // 타로 API는 인증 없이 접근 가능
            "/api/v1/health",
            "/swagger-ui",
            "/v3/api-docs"
        )
        
        return publicPaths.any { path.startsWith(it) }
    }
    
    private fun isProtectedPath(path: String): Boolean {
        // 명시적으로 보호가 필요한 경로
        val protectedPaths = listOf(
            "/api/v1/user/profile",
            "/api/v1/admin"
        )
        
        return protectedPaths.any { path.startsWith(it) }
    }
}
