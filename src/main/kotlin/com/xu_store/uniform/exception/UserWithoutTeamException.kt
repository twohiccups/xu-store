package com.xu_store.uniform.exception

class UserWithoutTeamException(val userId: Long?, message: String? = "User $userId doesn't belong to the teams") : RuntimeException(message)