package com.xu_store.uniform.util

import com.xu_store.uniform.config.JwtConfig
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class JwtCookieHelper(private val jwtConfig: JwtConfig) {

    private val accessTokenCookieName = "accessToken"
    private val cookiePath = "/"

    /** Creates the HttpOnly, Secure cookie for access token */
    fun createAccessTokenCookie(token: String): ResponseCookie {
        return ResponseCookie.from(accessTokenCookieName, token)
            .httpOnly(true)
            .secure(true)
            .path(cookiePath)
            .sameSite("Lax")
            .maxAge(jwtConfig.expirationPeriod / 1000) // Convert ms to seconds
            .build()
    }

    /** Creates a cookie that clears the access token on client */
    fun clearAccessTokenCookie(): ResponseCookie {
        return ResponseCookie.from(accessTokenCookieName, "")
            .httpOnly(true)
            .secure(true)
            .path(cookiePath)
            .sameSite("Lax")
            .maxAge(0) // Expire immediately
            .build()
    }
}
