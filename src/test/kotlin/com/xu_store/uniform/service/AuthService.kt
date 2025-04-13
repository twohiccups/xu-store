package com.xu_store.uniform.service

import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.UserRepository
import com.xu_store.uniform.security.JwtService
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class AuthServiceTests () {

    private val mockAuthentication = mock<Authentication>()
    private val userRepository = mock(UserRepository::class.java)
    private val passwordEncoder = mock(PasswordEncoder::class.java)
    private val jwtService = mock(JwtService::class.java)
    private val authenticationManager = mock(AuthenticationManager::class.java)
    private val authService = AuthService(
        userRepository = userRepository,
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

    private val emtpyUser = User(
        id = 0,
        email = "",
        passwordHash = "",
        role = "",
        storeCredits = 0,
        team = null,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Test
    fun `given user registration, when username and password ok then registration succeeds`() {

        whenever(passwordEncoder.encode(testPassword)).thenReturn(testPasswordHash)
        whenever(userRepository.findByEmail(testUsername)).thenReturn(null)
        whenever(userRepository.save(any())).thenAnswer { invocation ->
            val user = invocation.getArgument<User>(0)
            user.copy(id = 1L)
        }

        authService.registerUser(testUsername, testPassword)

        val userCaptor = argumentCaptor<User>()

        verify(userRepository, times(1)).save(userCaptor.capture())
        val savedUser = userCaptor.firstValue
        assertEquals(savedUser.email, testUsername)
        assertEquals(savedUser.passwordHash, testPasswordHash)
    }

    @Test
    fun `given user registration, when username is empty then registration fails`() {
        assertFailsWith<IllegalArgumentException> {   authService.registerUser("", testPassword) }
    }

    @Test
    fun `given user registration, when password is empty then registration fails`() {
        assertFailsWith<IllegalArgumentException> {   authService.registerUser(testUsername, "") }
    }

    @Test
    fun `given user registration, when username already exists then registration fails`() {
        whenever(userRepository.findByEmail(testUsername)).thenReturn(existingUser)
        assertFailsWith<IllegalArgumentException> {   authService.registerUser("user", "pass") }
    }

    @Test
    fun `given user login, when good credentials then token is returned`() {
        whenever(authenticationManager.authenticate(any())).thenReturn(mockAuthentication)
        whenever(mockAuthentication.isAuthenticated).thenReturn(true)
        whenever(jwtService.generateToken(testUsername)).thenReturn(testJwt)
        authService.loginUser(testUsername, testPassword)
        verify(jwtService, times(1)).generateToken(testUsername)
    }

    @Test
    fun `given user login, when bad credentials then exception is thrown`() {
        whenever(authenticationManager.authenticate(any())).thenReturn(mockAuthentication)
        whenever(mockAuthentication.isAuthenticated).thenReturn(false)
        whenever(jwtService.generateToken(testUsername)).thenReturn(testJwt)
        assertFailsWith<UsernameNotFoundException> {   authService.loginUser(testUsername, testPassword) }
    }

}