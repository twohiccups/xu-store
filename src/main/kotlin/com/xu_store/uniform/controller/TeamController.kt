package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.CreateTeamRequest
import com.xu_store.uniform.dto.TeamDetailResponse
import com.xu_store.uniform.dto.TeamResponse
import com.xu_store.uniform.dto.UpdateTeamRequest
import com.xu_store.uniform.dto.UserResponse
import com.xu_store.uniform.service.TeamService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/teams")
class TeamController(
    private val teamService: TeamService
) {

    // GET team details (including list of users) by team id.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{teamId}")
    fun getTeamById(@PathVariable teamId: Long): ResponseEntity<TeamDetailResponse> {
        val teamOpt = teamService.findTeamById(teamId)
        if (teamOpt.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        val teamDetail = TeamDetailResponse.from(teamOpt.get())
        return ResponseEntity.ok(teamDetail)
    }

    // 1) Create New Team
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createTeam(@RequestBody request: CreateTeamRequest): ResponseEntity<TeamResponse> {
        val team = teamService.createTeam(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(TeamResponse.from(team))
    }

    // 3) Edit Team
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{teamId}")
    fun updateTeam(
        @PathVariable teamId: Long,
        @RequestBody request: UpdateTeamRequest
    ): ResponseEntity<TeamResponse> {
        val team = teamService.updateTeam(teamId, request)
        return ResponseEntity.ok(TeamResponse.from(team))
    }

    // 2) Delete Team
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{teamId}")
    fun deleteTeam(@PathVariable teamId: Long): ResponseEntity<Void> {
        teamService.deleteTeam(teamId)
        return ResponseEntity.noContent().build()
    }

    // 4) Add User to the Team
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
}
