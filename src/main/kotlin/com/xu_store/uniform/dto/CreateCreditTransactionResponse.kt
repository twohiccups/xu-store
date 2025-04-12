package com.xu_store.uniform.dto

data class CreateCreditTransactionResponse (
    val success: Boolean,
    val error: String? = null,
    val creditTransaction: CreditTransactionResponse? = null
)