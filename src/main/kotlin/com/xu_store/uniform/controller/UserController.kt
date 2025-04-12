package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.ShoppingInfoResponse
import com.xu_store.uniform.dto.UserResponse
import com.xu_store.uniform.security.CustomUserDetails
import com.xu_store.uniform.service.TeamService
import com.xu_store.uniform.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val teamService: TeamService
) {


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/no-teams")
    fun getUsersWithoutTeams(): List<UserResponse> {
        val userResponseList = userService.getUsersWithoutTeams().map {
                user -> UserResponse.from((user))
        }
        return userResponseList
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/shopping-info")
    fun getCurrentShoppingInfo(): ResponseEntity<ShoppingInfoResponse> {
        val authorization = SecurityContextHolder.getContext().authentication
        val email =  (authorization.principal as CustomUserDetails).username
        val user = userService.getUserByEmail(email)
        requireNotNull(user) {"User doesn't exist"}

        return ResponseEntity.ok(
            ShoppingInfoResponse(
                storeCredits = requireNotNull(user.storeCredits),
                shippingFee = requireNotNull(user.team?.shippingFee)
        ))
    }
}

