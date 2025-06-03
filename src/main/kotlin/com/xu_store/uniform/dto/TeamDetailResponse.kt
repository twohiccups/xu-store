package com.xu_store.uniform.dto

import com.xu_store.uniform.model.Team
import java.time.Instant


data class TeamDetailResponse(
    val id: Long,
    val name: String,
    val shippingFee: Long,
    val createdAt: Instant
,
    val updatedAt: Instant
,
    val users: List<UserResponse>
) {
    companion object {
        fun from(team: Team): TeamDetailResponse {
            return TeamDetailResponse(
                id = requireNotNull(team.id) {"Team Id must not be null"},
                name = team.name,
                createdAt = team.createdAt,
                updatedAt = team.updatedAt,
                shippingFee = team.shippingFee,
                users = team.users.map { UserResponse.from(it) }
            )
        }
    }
}