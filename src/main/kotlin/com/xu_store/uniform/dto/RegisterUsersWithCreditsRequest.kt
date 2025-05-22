package com.xu_store.uniform.dto

import com.xu_store.uniform.service.TeamService

data class BulkRegisterRequest(
    val registerUsersRequest: List<RegisterUserWithCreditsRequest>,
)

data class RegisterUserWithCreditsRequest (
    val authRequest: AuthRequest,
    val storeCredits: Long
)


