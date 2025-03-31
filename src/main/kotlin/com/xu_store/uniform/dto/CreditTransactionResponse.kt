package com.xu_store.uniform.dto

import com.xu_store.uniform.model.CreditTransaction

data class CreditTransactionResponse (
    val id: Long,
    val user: UserResponse
) {
    companion object {
        fun from(creditTransaction: CreditTransaction) : CreditTransactionResponse {
            return CreditTransactionResponse(
                id = requireNotNull(creditTransaction.id),
                user = UserResponse.from(creditTransaction.user)
            )
        }
    }
}
