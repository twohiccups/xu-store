package com.xu_store.uniform.dto

import com.xu_store.uniform.model.Team

data class TeamsResponse(
    val teams: List<TeamDetailResponse>
) {
    companion object {
        fun from(teams: List<Team>): TeamsResponse {
            return TeamsResponse(
                teams = teams.map { team ->
                    TeamDetailResponse.from(team)
                }
            )
        }
    }
}






