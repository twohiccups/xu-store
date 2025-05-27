package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.AuthRequest
import com.xu_store.uniform.service.AuthService
import com.xu_store.uniform.util.JwtCookieHelper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtCookieHelper: JwtCookieHelper
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@RequestBody request: AuthRequest): Long? {
        val user = authService.registerUser(request.username, request.password)
        return user.id
    }


    @PostMapping("/login")
    fun loginUser(@RequestBody authRequest: AuthRequest, response: HttpServletResponse): ResponseEntity<Map<String, String>> {
        val token = authService.loginUser(authRequest.username, authRequest.password)
        val cookie = jwtCookieHelper.createAccessTokenCookie(token)
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        val body = mapOf("message" to "Login successful")
        return ResponseEntity.ok(body)
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Map<String, String>> {
        val expiredCookie = jwtCookieHelper.deleteAccessTokenCookie()
        response.setHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString())
        val body = mapOf("message" to "Logout successful")
        return ResponseEntity.ok(body)
    }

}
