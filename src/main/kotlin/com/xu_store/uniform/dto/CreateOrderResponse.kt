package com.xu_store.uniform.dto

data class CreateOrderResponse (
    val success: Boolean,
    val errorMessage: String?,
    val orderResponse: OrderResponse?
)