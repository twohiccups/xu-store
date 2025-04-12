package com.xu_store.uniform.service

import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserService (
    private val userRepository: UserRepository,
    ){

    fun getUserById(userId: Long) : Optional<User> {
        return userRepository.findById(userId)
    }

    fun getUsersWithoutTeams() : List<User> {
        return userRepository.findByTeamIsNull()
    }

    fun getUserByEmail(email: String) : User? {
        return userRepository.findByEmail(email)
    }

    fun saveUser(user: User): User {
        return userRepository.save(user)
    }

}