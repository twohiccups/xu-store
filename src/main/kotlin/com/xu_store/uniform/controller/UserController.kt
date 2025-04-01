package com.xu_store.uniform.controller

import com.example.demo.security.CustomUserDetails
import com.xu_store.uniform.dto.UserResponse
import com.xu_store.uniform.model.User
import com.xu_store.uniform.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {


    @GetMapping("/no-teams")
    fun getUsersWithoutTeams(): List<UserResponse> {
        val userResponseList = userService.getUsersWithoutTeams().map {
                user -> UserResponse.from((user))
        }
        return userResponseList
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/store-credits")
    fun getCurrentUserStoreCredits(): Long {
        val authorization = SecurityContextHolder.getContext().authentication
        val email =  (authorization.principal as CustomUserDetails).username
        val user = userService.getUserByEmail(email)
        requireNotNull(user) {"User doesn't exist"}
        return user.storeCredits
    }
}

