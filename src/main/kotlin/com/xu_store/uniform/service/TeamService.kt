package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateTeamRequest
import com.xu_store.uniform.dto.UpdateTeamRequest
import com.xu_store.uniform.model.Team
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.TeamRepository
import com.xu_store.uniform.repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

import java.util.*

@Service
class TeamService(
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository,
) {

    fun createTeam(request: CreateTeamRequest): Team {
        val team = Team(
            name = request.name,
            shippingFee = request.shippingFee,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        return teamRepository.save(team)
    }

    fun updateTeam(teamId: Long, request: UpdateTeamRequest): Team {
        val existingTeam = teamRepository.findById(teamId)
            .orElseThrow { RuntimeException("Team not found with id: $teamId") }
        // Update only the fields that are provided.
        val updatedTeam = existingTeam.copy(
            name = request.name,
            shippingFee = request.shippingFee,
            updatedAt = Instant.now()
        )
        return teamRepository.save(updatedTeam)
    }

    fun deleteTeam(teamId: Long) {
        teamRepository.deleteById(teamId)
    }

    @Transactional
    fun addUserToTeam(teamId: Long, userId: Long): User {
        val team = teamRepository.findById(teamId)
            .orElseThrow { RuntimeException("Team not found with id: $teamId") }
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found with id: $userId") }
        // Update the user's team.
        val updatedUser = user.copy(team = team)
        return userRepository.save(updatedUser)
    }

    @Transactional
    fun removeUserFromTeam(teamId: Long, userId: Long): User {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found with id: $userId") }
        if (user.team == null || user.team?.id != teamId) {
            throw RuntimeException("User is not a member of team with id: $teamId")
        }
        val updatedUser = user.copy(team = null)
        return userRepository.save(updatedUser)
    }

    fun getTeamById(teamId: Long): Team {
        return teamRepository.findById(teamId)
            .orElseThrow { UsernameNotFoundException("Team with ID $teamId was not found") }
    }

    fun getAllTeams(): List<Team> {
        return teamRepository.findAllByOrderByCreatedAtDesc()
    }
}
