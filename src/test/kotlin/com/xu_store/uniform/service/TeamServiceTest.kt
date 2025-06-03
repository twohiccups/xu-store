package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateTeamRequest
import com.xu_store.uniform.dto.UpdateTeamRequest
import com.xu_store.uniform.model.Team
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.TeamRepository
import com.xu_store.uniform.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.time.Instant
import java.time.temporal.ChronoUnit

import java.util.*

class TeamServiceTest {

 private val teamRepository: TeamRepository = mock(TeamRepository::class.java)
 private val userRepository: UserRepository = mock(UserRepository::class.java)
 private val teamService = TeamService(teamRepository, userRepository)

 @Test
 fun `createTeam should save and return new team`() {
  // Arrange
  val request = CreateTeamRequest(name = "Team A", shippingFee = 0)
  // We simulate the repository saving the team by returning a copy with an ID.
  val teamToSave = Team(
   id = null,
   name = request.name,
   createdAt = Instant.now(),
   updatedAt = Instant.now()
  )
  val savedTeam = teamToSave.copy(id = 1L)
  whenever(teamRepository.save(any(Team::class.java))).thenReturn(savedTeam)

  // Act
  val result = teamService.createTeam(request)

  // Assert
  assertNotNull(result)
  assertEquals("Team A", result.name)
  assertNotNull(result.id)
  verify(teamRepository, times(1)).save(any(Team::class.java))
 }

 @Test
 fun `updateTeam should update existing team`() {
  // Arrange
  val teamId = 1L
  val existingTeam = Team(
   id = teamId,
   name = "Old Name",
   createdAt = Instant.now().minus(1, ChronoUnit.DAYS),
   updatedAt = Instant.now().minus(1, ChronoUnit.DAYS)
  )
  val updateRequest = UpdateTeamRequest(name = "New Name", shippingFee = 0)
  whenever(teamRepository.findById(teamId)).thenReturn(Optional.of(existingTeam))
  whenever(teamRepository.save(any(Team::class.java))).thenAnswer { it.arguments[0] as Team }

  // Act
  val updatedTeam = teamService.updateTeam(teamId, updateRequest)

  // Assert
  assertNotNull(updatedTeam)
  assertEquals("New Name", updatedTeam.name)
  assertTrue(updatedTeam.updatedAt.isAfter(existingTeam.updatedAt))
  verify(teamRepository, times(1)).findById(teamId)
  verify(teamRepository, times(1)).save(any(Team::class.java))
 }

 @Test
 fun `deleteTeam should call repository deleteById`() {
  // Arrange
  val teamId = 1L

  // Act
  teamService.deleteTeam(teamId)

  // Assert
  verify(teamRepository, times(1)).deleteById(teamId)
 }

 @Test
 fun `addUserToTeam should assign team to user`() {
  // Arrange
  val teamId = 1L
  val userId = 2L
  val team = Team(
   id = teamId,
   name = "Team A",
   createdAt = Instant.now(),
   updatedAt = Instant.now()
  )
  val user = User(
   id = userId,
   email = "user@example.com",
   passwordHash = "hash",
   role = "USER",
   storeCredits = 0,
   team = null,
   createdAt = Instant.now(),
   updatedAt = Instant.now()
  )
  whenever(teamRepository.findById(teamId)).thenReturn(Optional.of(team))
  whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
  whenever(userRepository.save(any(User::class.java))).thenAnswer { it.arguments[0] as User }

  // Act
  val updatedUser = teamService.addUserToTeam(teamId, userId)

  // Assert
  assertNotNull(updatedUser)
  assertNotNull(updatedUser.team)
  assertEquals(teamId, updatedUser.team?.id)
  verify(teamRepository, times(1)).findById(teamId)
  verify(userRepository, times(1)).findById(userId)
  verify(userRepository, times(1)).save(any(User::class.java))
 }

 @Test
 fun `removeUserFromTeam should remove team from user`() {
  // Arrange
  val teamId = 1L
  val userId = 2L
  val team = Team(
   id = teamId,
   name = "Team A",
   createdAt = Instant.now(),
   updatedAt = Instant.now()
  )
  val user = User(
   id = userId,
   email = "user@example.com",
   passwordHash = "hash",
   role = "USER",
   storeCredits = 0,
   team = team,
   createdAt = Instant.now(),
   updatedAt = Instant.now()
  )
  whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
  whenever(userRepository.save(any(User::class.java))).thenAnswer { it.arguments[0] as User }

  // Act
  val updatedUser = teamService.removeUserFromTeam(teamId, userId)

  // Assert
  assertNotNull(updatedUser)
  assertNull(updatedUser.team)
  verify(userRepository, times(1)).findById(userId)
  verify(userRepository, times(1)).save(any(User::class.java))
 }

 @Test
 fun `removeUserFromTeam should throw exception if user not in team`() {
  // Arrange
  val teamId = 1L
  val userId = 2L
  // Create a user with no team assigned.
  val user = User(
   id = userId,
   email = "user@example.com",
   passwordHash = "hash",
   role = "USER",
   storeCredits = 0,
   team = null,
   createdAt = Instant.now(),
   updatedAt = Instant.now()
  )
  whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))

  // Act & Assert
  val exception = assertThrows<RuntimeException> {
   teamService.removeUserFromTeam(teamId, userId)
  }
  assertEquals("User is not a member of team with id: $teamId", exception.message)
 }

 @Test
 fun `findTeamById should return team if exists`() {
  // Arrange
  val teamId = 1L
  val team = Team(
   id = teamId,
   name = "Team A",
   createdAt = Instant.now(),
   updatedAt = Instant.now()
  )
  whenever(teamRepository.findById(teamId)).thenReturn(Optional.of(team))
}}
