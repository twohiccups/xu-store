package com.xu_store.uniform.service

import com.xu_store.uniform.dto.ShoppingInfoResponse
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
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

    fun getUserByEmail(email: String) : User {
        val user = userRepository.findByEmail(email)
        return user ?: throw UsernameNotFoundException("User $email was not found")
    }

    fun saveUser(user: User): User {
        return userRepository.save(user)
    }

    fun getCurrentShoppingInfo(user: User): ShoppingInfoResponse {
        val currentTeam = requireNotNull(user.team)    { IllegalArgumentException("User doesn't belong to any team")}
        return ShoppingInfoResponse(
            shippingFee = currentTeam.shippingFee,
            storeCredits = user.storeCredits,
        )
    }


}