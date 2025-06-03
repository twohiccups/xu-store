package com.xu_store.uniform.service

import com.xu_store.uniform.dto.AuthRequest
import com.xu_store.uniform.dto.CreditTransactionRequest
import com.xu_store.uniform.dto.RegisterUserWithCreditsRequest
import com.xu_store.uniform.dto.BulkRegisterRequest
import com.xu_store.uniform.model.Team
import com.xu_store.uniform.model.User
import org.mockito.kotlin.*
import java.time.Instant

import kotlin.test.Test
import kotlin.test.assertEquals

class BulkRegisterServiceTest {

    private val userService = mock<UserService>()
    private val authService = mock<AuthService>()
    private val teamService = mock<TeamService>()
    private val creditService = mock<CreditService>()

    private val bulkRegisterService = BulkRegisterService(
        userService = userService,
        authService = authService,
        teamService = teamService,
        creditService = creditService
    )

    private val teamId: Long = 1
    private val team = Team(id = teamId, name = "Team X", createdAt = Instant.now(), updatedAt = Instant.now())

    private val existingUser = User(
        id = 10,
        email = "existing@example.com",
        passwordHash = "hash",
        role = "USER",
        storeCredits = 100,
        team = null,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    private val newUser = User(
        id = 11,
        email = "new@example.com",
        passwordHash = "newpass",
        role = "USER",
        storeCredits = 0,
        team = null,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    @Test
    fun `given existing user, when processed then balance is reset and team is set`() {
        val request = BulkRegisterRequest(
            registerUsersRequest = listOf(
                RegisterUserWithCreditsRequest(
                    authRequest = AuthRequest("existing@example.com", "irrelevant"),
                    storeCredits = 300
                )
            )
        )

        whenever(teamService.getTeamById(1)).thenReturn(team)
        whenever(userService.doesUserExist("existing@example.com")).thenReturn(true)
        whenever(userService.getUserByEmail("existing@example.com")).thenReturn(existingUser)
        whenever(userService.saveUser(any())).thenReturn(existingUser.copy(team = team))

        bulkRegisterService.processRegistrationList(teamId, request)

        // Reset credits to 0
        verify(creditService).adjustCredits(
            CreditTransactionRequest(
                userId = requireNotNull(existingUser.id),
                amount = -100,
                description = "Bulk registration: email is re-registered, resetting the balance."
            )
        )

        // Set new credits
        verify(creditService).adjustCredits(
            CreditTransactionRequest(
                userId = requireNotNull(existingUser.id),
                amount = 300,
                description = "Bulk registration: setting initial amount"
            )
        )

        // User is updated with new team
        val userCaptor = argumentCaptor<User>()
        verify(userService).saveUser(userCaptor.capture())
        assertEquals(team, userCaptor.firstValue.team)
    }

    @Test
    fun `given new user, when processed then they are registered and credited`() {
        val request = BulkRegisterRequest(
            registerUsersRequest = listOf(
                RegisterUserWithCreditsRequest(
                    authRequest = AuthRequest("new@example.com", "secret"),
                    storeCredits = 200
                )
            )
        )

        whenever(teamService.getTeamById(1)).thenReturn(team)
        whenever(userService.doesUserExist("new@example.com")).thenReturn(false)
        whenever(authService.registerUser("new@example.com", "secret")).thenReturn(newUser)
        whenever(userService.saveUser(any())).thenReturn(newUser.copy(team = team))

        bulkRegisterService.processRegistrationList(teamId, request)

        // Verify credit assignment
        verify(creditService).adjustCredits(
            CreditTransactionRequest(
                userId = requireNotNull(newUser.id),
                amount = 200,
                description = "Bulk registration: setting initial amount"
            )
        )

        // Verify saved user has team
        val userCaptor = argumentCaptor<User>()
        verify(userService).saveUser(userCaptor.capture())
        assertEquals(team, userCaptor.firstValue.team)
    }
}
