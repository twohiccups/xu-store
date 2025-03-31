package com.xu_store.uniform.dto

data class CreditTransactionRequest (
    val userId: Long,
    val amount: Long,
    val description: String?
)