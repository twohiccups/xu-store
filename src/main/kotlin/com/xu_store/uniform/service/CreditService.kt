package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreditTransactionRequest
import com.xu_store.uniform.model.CreditTransaction
import com.xu_store.uniform.model.Order
import com.xu_store.uniform.repository.CreditTransactionRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant


@Service
class CreditService (
    private val creditTransactionRepository: CreditTransactionRepository,
    private val userService: UserService
) {

    fun getCreditTransactionHistoryByUserId(userId: Long) : List<CreditTransaction> {
        return creditTransactionRepository.findAllByUserId(userId)
    }


    private fun createCreditTransaction(userId: Long, amount: Long, description: String?, order: Order?) {
        val user = userService.getUserById(userId) // Efficient proxy, no full fetch
        creditTransactionRepository.save(
            CreditTransaction(
                user = user,
                order = order,
                amount = amount,
                description = description,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )
    }

    @Transactional
    fun deductCredits(userId: Long, amount: Long, description: String?) {
        userService.deductUserCreditsOrThrow(userId, amount) // ✅ Atomic + safe
        createCreditTransaction(userId, -amount, description,  null)
    }


    @Transactional
    fun adjustCreditsByAdmin(request: CreditTransactionRequest): CreditTransaction {
        return if (request.amount < 0) {
            // ✅ Deduct using atomic-safe method (flip sign)
            val amountToDeduct = -request.amount
            deductCredits(request.userId, amountToDeduct, request.description)
            // You may choose to return a placeholder transaction if needed, or fetch it
            CreditTransaction(
                user = userService.getUserById(request.userId),
                order = null,
                amount = request.amount,
                description = request.description,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        } else {
            // ✅ Add credits using simple logic
            userService.addUserCreditsOrThrow(request.userId, request.amount)

            val user = userService.getUserById(request.userId)
            return creditTransactionRepository.save(
                CreditTransaction(
                    user = user,
                    order = null,
                    amount = request.amount,
                    description = request.description,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
            )
        }
    }

    fun logOrderTransaction(userId: Long, amount: Long, description: String, order: Order) {
        val user = userService.getUserById(userId)
        creditTransactionRepository.save(
            CreditTransaction(
                user = user,
                order = order,
                amount = amount,
                description = description,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )
    }


}