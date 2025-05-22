package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.*
import com.xu_store.uniform.service.AuthService
import com.xu_store.uniform.service.BulkRegisterService
import com.xu_store.uniform.service.TeamService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/teams")
class TeamController(
    private val teamService: TeamService,
    private val bulkRegisterService: BulkRegisterService
    ) {
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getAllTeams(): ResponseEntity<TeamsResponse> {
        val teams = teamService.getAllTeams()
        return ResponseEntity.ok(TeamsResponse.from(teams))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{teamId}")
    fun getTeamById(@PathVariable teamId: Long): ResponseEntity<TeamDetailResponse> {
        val team = teamService.getTeamById(teamId)
        val teamDetail = TeamDetailResponse.from(team)
        return ResponseEntity.ok(teamDetail)
    }

    // 1) Create New Team
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createTeam(@RequestBody request: CreateTeamRequest): ResponseEntity<TeamResponse> {
        val team = teamService.createTeam(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(TeamResponse.from(team))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{teamId}")
    fun updateTeam(
        @PathVariable teamId: Long,
        @RequestBody request: UpdateTeamRequest
    ): ResponseEntity<TeamDetailResponse> {
        val team = teamService.updateTeam(teamId, request)
        return ResponseEntity.ok(TeamDetailResponse.from(team))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{teamId}")
    fun deleteTeam(@PathVariable teamId: Long): ResponseEntity<Void> {
        teamService.deleteTeam(teamId)
        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{teamId}/users/{userId}")
    fun addUserToTeam(
        @PathVariable teamId: Long,
        @PathVariable userId: Long
    ): ResponseEntity<UserResponse> {
        val updatedUser = teamService.addUserToTeam(teamId, userId)
        return ResponseEntity.ok(UserResponse.from(updatedUser))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{teamId}/users/{userId}")
    fun removeUserFromTeam(
        @PathVariable teamId: Long,
        @PathVariable userId: Long
    ): ResponseEntity<UserResponse> {
        val updatedUser = teamService.removeUserFromTeam(teamId, userId)
        return ResponseEntity.ok(UserResponse.from(updatedUser))
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{teamId}/bulkRegisterUsers")
    fun registerUsersWithCredits(
        @PathVariable teamId: Long,
        registerUsersWithCreditsRequest: RegisterUsersWithCreditsRequest
    ): ResponseEntity<Void> {
        bulkRegisterService.processRegistrationList(teamId, registerUsersWithCreditsRequest)
        return ResponseEntity.ok().build()
    }


}
