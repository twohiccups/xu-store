package com.xu_store.uniform.util

import com.xu_store.uniform.config.JwtConfig
import org.apache.tomcat.util.http.SameSiteCookies
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
            .sameSite(SameSiteCookies.NONE.toString())
            .maxAge(jwtConfig.expirationPeriod / 1000) // Convert ms to seconds
            .build()
    }

    fun deleteAccessTokenCookie(): ResponseCookie {
        return ResponseCookie.from(accessTokenCookieName, "")
            .domain(jwtConfig.cookieDomain)
            .httpOnly(true)
            .secure(true)
            .path(cookiePath)
            .sameSite(SameSiteCookies.NONE.toString())
            .maxAge(0)
            .build()
    }
}
