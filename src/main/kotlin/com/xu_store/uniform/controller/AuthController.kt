package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.AuthRequest
import com.xu_store.uniform.dto.JwtResponse
import com.xu_store.uniform.service.AuthService
import com.xu_store.uniform.util.JwtCookieHelper
import jakarta.servlet.http.HttpServletResponse
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun authenticateAndGetToken(
        @RequestBody request: AuthRequest,
        response: HttpServletResponse
    ) {
        val token = authService.loginUser(request.username, request.password)
        val cookie = jwtCookieHelper.createAccessTokenCookie(token)
        response.addHeader("Set-Cookie", cookie.toString())
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(response: HttpServletResponse)  {
        val clearCookie = jwtCookieHelper.clearAccessTokenCookie()
        response.addHeader("Set-Cookie", clearCookie.toString())
    }
}
