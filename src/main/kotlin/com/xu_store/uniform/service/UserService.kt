package com.xu_store.uniform.service

import com.xu_store.uniform.dto.ShoppingInfoResponse
import com.xu_store.uniform.exception.InsufficientCreditsException
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
    private val userRepository: UserRepository,
    ){

    fun getUserById(userId: Long) : User {
        return userRepository.findById(userId).orElseThrow { EntityNotFoundException("User was not found")}
    }

    fun getUsersWithoutTeams() : List<User> {
        return userRepository.findByTeamIsNull()
    }

    fun getUserByEmail(email: String) : User {
        val user = userRepository.findByEmail(email)
        return user ?: throw UsernameNotFoundException("User $email was not found")
    }

    fun doesUserExist(email: String): Boolean {
        val user = userRepository.findByEmail(email)
        return user != null
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

    @Transactional
    fun deductUserCreditsOrThrow(userId: Long, amount: Long) {
        val rowsUpdated = userRepository.deductUserCredits(userId, amount)
        if (rowsUpdated == 0) {
            throw InsufficientCreditsException("User $userId has insufficient store credits to deduct $amount")
        }
    }

    @Transactional
    fun addUserCreditsOrThrow(userId: Long, amount: Long) {
        require(amount > 0) { "Amount must be positive for credit addition" }
        userRepository.incrementUserCredits(userId, amount)
    }


}