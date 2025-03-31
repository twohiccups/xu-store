package com.xu_store.uniform.dto

import com.xu_store.uniform.model.Team
import java.time.LocalDateTime

data class TeamResponse(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(team: Team): TeamResponse {
            return TeamResponse(
                id = requireNotNull(team.id) {"Team Id must not be null"},
                name = team.name,
                createdAt = team.createdAt,
                updatedAt = team.updatedAt
            )
        }
    }
}

