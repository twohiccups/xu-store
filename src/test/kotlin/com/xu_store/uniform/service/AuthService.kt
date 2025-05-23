package com.xu_store.uniform.service

import com.xu_store.uniform.model.User
import com.xu_store.uniform.security.JwtService
import org.mockito.kotlin.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthServiceTests {

    private val mockAuthentication = mock<Authentication>()
    private val userService = mock<UserService>()  // ✅ Kotlin-style mock
    private val passwordEncoder = mock<PasswordEncoder>()
    private val jwtService = mock<JwtService>()
    private val authenticationManager = mock<AuthenticationManager>()

    private val authService = AuthService(
        userService = userService,
        passwordEncoder = passwordEncoder,
        jwtService = jwtService,
        authenticationManager = authenticationManager
    )

    private val testUsername = "user"
    private val testPassword = "pass"
    private val testPasswordHash = "hash"
    private val testJwt = "crazytoken"

    private val existingUser = User(
        id = 15,
        email = testUsername,
        passwordHash = testPasswordHash,
        role = "",
        storeCredits = 0,
        team = null,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Test
    fun  `given user registration, when username and password ok then registration succeeds`() {
        whenever(passwordEncoder.encode(testPassword)).thenReturn(testPasswordHash)
        whenever(userService.doesUserExist(testUsername)).thenReturn(false)
        whenever(userService.saveUser(any())).thenAnswer { invocation ->
            val user = invocation.getArgument<User>(0)
            user.copy(id = 1L)
        }

        authService.registerUser(testUsername, testPassword)

        val userCaptor = argumentCaptor<User>()
        verify(userService).saveUser(userCaptor.capture())
        val savedUser = userCaptor.firstValue

        assertEquals(testUsername, savedUser.email)
        assertEquals(testPasswordHash, savedUser.passwordHash)
    }

    @Test
    fun `given user registration, when username is empty then registration fails`() {
        assertFailsWith<IllegalArgumentException> {
            authService.registerUser("", testPassword)
        }
    }

    @Test
    fun `given user registration, when password is empty then registration fails`() {
        assertFailsWith<IllegalArgumentException> {
            authService.registerUser(testUsername, "")
        }
    }

    @Test
    fun `given user registration, when username already exists then registration fails`() {
        whenever(userService.doesUserExist(testUsername)).thenReturn(true)
        assertFailsWith<IllegalArgumentException> {
            authService.registerUser(testUsername, testPassword)
        }
    }

//    @Test
//    fun `given user login, when good credentials then token is returned`() {
//        whenever(authenticationManager.authenticate(any())).thenReturn(mockAuthentication)
//        whenever(mockAuthentication.isAuthenticated).thenReturn(true)
//        whenever(jwtService.generateToken(testUsername)).thenReturn(testJwt)
//
//        val token = authService.loginUser(testUsername, testPassword)
//
//        assertEquals(testJwt, token)
//        verify(jwtService).generateToken(testUsername)
//    }

    @Test
    fun `given user login, when bad credentials then exception is thrown`() {
        whenever(authenticationManager.authenticate(any())).thenReturn(mockAuthentication)
        whenever(mockAuthentication.isAuthenticated).thenReturn(false)

        assertFailsWith<UsernameNotFoundException> {
            authService.loginUser(testUsername, testPassword)
        }
    }
}
