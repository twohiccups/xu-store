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
            .domain(jwtConfig.cookieDomain)
            .httpOnly(true)
            .secure(true)
            .path(cookiePath)
            .sameSite("Lax")
            .maxAge(jwtConfig.expirationPeriod / 1000) // Convert ms to seconds
            .build()
    }

    fun deleteAccessTokenCookie(): ResponseCookie {
        return ResponseCookie.from(accessTokenCookieName, "")
            .domain(jwtConfig.cookieDomain)      // same domain as createAccessTokenCookie
            .path(cookiePath)                   // same path
            .httpOnly(true)
            .secure(true)
            .sameSite(SameSiteCookies.LAX.toString())
            .maxAge(0)                          // expire immediately
            .build()
    }
}
