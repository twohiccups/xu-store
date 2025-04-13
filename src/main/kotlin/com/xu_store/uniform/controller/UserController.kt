package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.ShoppingInfoResponse
import com.xu_store.uniform.dto.UserResponse
import com.xu_store.uniform.security.CustomUserDetails
import com.xu_store.uniform.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
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
    fun getCurrentShoppingInfo(@AuthenticationPrincipal currentUser: CustomUserDetails): ResponseEntity<ShoppingInfoResponse> {
        val user = userService.getUserByEmail(currentUser.username)
        val shoppingInfoResponse = userService.getCurrentShoppingInfo(user)
        return ResponseEntity.ok(
            shoppingInfoResponse
        )
    }
}

