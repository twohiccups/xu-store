package com.xu_store.uniform.security

import com.xu_store.uniform.service.UserDetailsServiceImpl
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsServiceImpl
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Try to extract JWT from cookie
        val cookies = request.cookies
        val token = cookies?.firstOrNull { it.name == "accessToken" }?.value

        var username: String? = null

        if (token != null) {
            try {
                username = jwtService.extractUsername(token)
            } catch (e: io.jsonwebtoken.ExpiredJwtException) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token expired")
                return
            }
        }

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(username)
            if (jwtService.validateToken(token!!, userDetails)) {
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        filterChain.doFilter(request, response)
    }

}
