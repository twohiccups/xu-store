package com.xu_store.uniform.dto

import com.xu_store.uniform.service.TeamService

data class RegisterUsersWithCreditsRequest(
    val registerUsersRequest: List<RegisterUserWithCreditsRequest>,
    val teamId: Long
)

data class RegisterUserWithCreditsRequest (
    val authRequest: AuthRequest,
    val storeCredits: Long
)


