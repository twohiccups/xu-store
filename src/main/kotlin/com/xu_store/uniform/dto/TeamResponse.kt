package com.xu_store.uniform.dto

import com.xu_store.uniform.model.Team
import java.time.Instant


data class TeamResponse(
    val id: Long,
    val name: String,
    val shippingFee: Long,
    val createdAt: Instant
,
    val updatedAt: Instant

) {
    companion object {
        fun from(team: Team): TeamResponse {
            return TeamResponse(
                id = requireNotNull(team.id) {"Team Id must not be null"},
                name = team.name,
                shippingFee = team.shippingFee,
                createdAt = team.createdAt,
                updatedAt = team.updatedAt
            )
        }
    }
}

