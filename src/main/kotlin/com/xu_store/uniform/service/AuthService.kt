package com.xu_store.uniform.service

import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.UserRepository
import com.xu_store.uniform.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
) {

    fun registerUser(username: String, password:String): User {
        when {
            username.isEmpty() -> throw IllegalArgumentException("Username cannot be empty")
            password.isEmpty() -> throw IllegalArgumentException("Password cannot be empty")
            userService.doesUserExist(username) -> throw IllegalArgumentException("Username already exists")
        }

        val hashedPassword = passwordEncoder.encode(password)
        val newUser = User(
            email = username,
            passwordHash = hashedPassword,
        )
        return userService.saveUser((newUser))
    }

    /* Return JWT */
    fun loginUser(username: String, password: String): String {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, password)
        )

        if (authentication.isAuthenticated) {
            val userDetails = authentication.principal as UserDetails
            return jwtService.generateToken(userDetails)
        } else {
            throw UsernameNotFoundException("Invalid user request")
        }
    }




}