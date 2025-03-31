package com.xu_store.uniform.dto

import com.xu_store.uniform.model.Team
import java.time.LocalDateTime

data class TeamDetailResponse(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val users: List<UserResponse>
) {
    companion object {
        fun from(team: Team): TeamDetailResponse {
            return TeamDetailResponse(
                id = requireNotNull(team.id) {"Team Id must not be null"},
                name = team.name,
                createdAt = team.createdAt,
                updatedAt = team.updatedAt,
                users = team.users.map { UserResponse.from(it) }
            )
        }
    }
}