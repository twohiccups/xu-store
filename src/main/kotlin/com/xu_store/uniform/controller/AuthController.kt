package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.AuthRequest
import com.xu_store.uniform.dto.JwtResponse
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.UserRepository
import com.xu_store.uniform.security.JwtService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1")
class AuthController(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * Register a new user, storing only the hashed password in DB.
     * The role is optional here; you can adapt as needed.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@RequestBody request: AuthRequest): Long? {
        // Optionally check if user already exists
        if (request.username.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty")
        }

        if (userRepository.findByEmail(request.username) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")
        }

        if (request.password.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty")
        }

        // We store a hashed password, never plaintext
        val hashedPassword = passwordEncoder.encode(request.password)

        val newUser = User(
            email = request.username,
            passwordHash = hashedPassword,
        )
        val user = userRepository.save(newUser)
        return user.id
    }

    /**
     * Log in with username/password, return JWT if successful.
     */
    @PostMapping("/login")
    fun authenticateAndGetToken(@RequestBody request: AuthRequest): JwtResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )
        if (authentication.isAuthenticated) {
            val token = jwtService.generateToken(request.username)
            return JwtResponse(accessToken = token)
        } else {
            throw UsernameNotFoundException("Invalid user request!")
        }
    }

}
