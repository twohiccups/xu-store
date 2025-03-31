package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.UserResponse
import com.xu_store.uniform.model.User
import com.xu_store.uniform.service.UserService
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
}
