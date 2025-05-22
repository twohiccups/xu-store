package com.xu_store.uniform.dto

import com.xu_store.uniform.service.TeamService

data class RegisterUsersWithCreditsRequest(
    val registerUsersRequest: List<RegisterUserWithCreditsRequest>,
)

data class RegisterUserWithCreditsRequest (
    val authRequest: AuthRequest,
    val storeCredits: Long
)


