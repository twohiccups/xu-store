package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.AuthRequest
import com.xu_store.uniform.dto.JwtResponse
import com.xu_store.uniform.model.User
import com.xu_store.uniform.security.JwtService
import com.xu_store.uniform.service.AuthService
import com.xu_store.uniform.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@RequestBody request: AuthRequest): Long? {
        val user = authService.registerUser(request.username, request.password)
        return user.id
    }

    /** Log in with username/password, return JWT if successful */
    @PostMapping("/login")
    fun authenticateAndGetToken(@RequestBody request: AuthRequest): JwtResponse {
        val token = authService.loginUser(request.username, request.password)
        return JwtResponse(accessToken = token)
    }

}
