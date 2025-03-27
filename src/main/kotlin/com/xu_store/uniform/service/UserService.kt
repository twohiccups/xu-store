package com.xu_store.uniform.service

import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (
    val userRepository: UserRepository
){


    fun listUsersWithoutTeams() : List<User> {
        return userRepository.findByTeamIsNull()
    }

}