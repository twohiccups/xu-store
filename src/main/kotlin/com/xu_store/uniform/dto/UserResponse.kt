package com.xu_store.uniform.dto

import com.xu_store.uniform.model.User

data class UserResponse(
    val id: Long?,
    val email: String,
    val role: String
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                email = user.email,
                role = user.role
            )
        }
    }
}