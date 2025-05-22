package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreditTransactionRequest
import com.xu_store.uniform.model.CreditTransaction
import com.xu_store.uniform.repository.CreditTransactionRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException


@Service
class CreditService (
    private val creditTransactionRepository: CreditTransactionRepository,
    private val userService: UserService
) {

    fun getCreditTransactionHistoryByUserId(userId: Long) : List<CreditTransaction> {
        return creditTransactionRepository.findAllByUserId(userId)
    }


    @Transactional
    fun adjustCredits(creditTransactionRequest: CreditTransactionRequest): CreditTransaction {

        val user = userService.getUserById(creditTransactionRequest.userId)
        val newBalance = user.storeCredits + creditTransactionRequest.amount

        require(newBalance >= 0) {"New Balance must be non negative"}

        val creditTransaction = CreditTransaction(
            user = user,
            order = null,
            amount = creditTransactionRequest.amount,
            description = creditTransactionRequest.description,
            createdAt = java.time.LocalDateTime.now(),
            updatedAt = java.time.LocalDateTime.now()
        )

        val savedCreditTransaction = creditTransactionRepository.save(creditTransaction)

        val userCopy = user.copy(storeCredits = newBalance)
        userService.saveUser(userCopy)

        return savedCreditTransaction

    }

}