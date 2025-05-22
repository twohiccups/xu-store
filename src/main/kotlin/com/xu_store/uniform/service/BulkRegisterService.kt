package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreditTransactionRequest
import com.xu_store.uniform.dto.BulkRegisterRequest
import com.xu_store.uniform.model.User
import org.springframework.stereotype.Service

@Service
class BulkRegisterService(
    private val userService: UserService,
    private val authService: AuthService,
    private val teamService: TeamService,
    private val creditService: CreditService

) {

    fun processRegistrationList(teamId: Long, registerRequest: BulkRegisterRequest) {
        val team = teamService.getTeamById(teamId)
        registerRequest.registerUsersRequest.forEach { request ->

            val user: User = when {
                userService.doesUserExist(request.authRequest.username) -> {
                    val existingUser = userService.getUserByEmail(request.authRequest.username)
                    val resetBalanceRequest = CreditTransactionRequest(
                        userId = requireNotNull(existingUser.id),
                        amount = -existingUser.storeCredits,
                        description = "Bulk registration: email is re-registered, resetting the balance."
                    )
                    creditService.adjustCredits(resetBalanceRequest)
                    existingUser
                }
                else -> authService.registerUser(request.authRequest.username, request.authRequest.password)
            }

            userService.saveUser(
                user.copy(
                    team = team
                )
            )

            val newBalanceRequest = CreditTransactionRequest(
                userId = requireNotNull(user.id),
                amount = request.storeCredits,
                description = "Bulk registration: setting initial amount"
            )
            creditService.adjustCredits(newBalanceRequest)
            }
        }
    }
