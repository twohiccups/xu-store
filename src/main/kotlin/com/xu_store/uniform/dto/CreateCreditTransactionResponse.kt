package com.xu_store.uniform.dto

import com.xu_store.uniform.model.CreditTransaction
import com.xu_store.uniform.model.User

data class CreateCreditTransactionResponse (
    val success: Boolean,
    val error: String? = null,
    val creditTransaction: CreditTransactionResponse? = null
)