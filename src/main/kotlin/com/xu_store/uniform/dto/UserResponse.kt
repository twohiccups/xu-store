package com.xu_store.uniform.dto

import com.xu_store.uniform.model.User

data class UserResponse(
    val id: Long,
    val email: String,
    val role: String,
    val storeCredits: Long
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = requireNotNull(user.id),
                email = user.email,
                role = user.role,
                storeCredits = user.storeCredits
            )
        }
    }
}