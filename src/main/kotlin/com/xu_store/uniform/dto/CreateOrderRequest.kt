package com.xu_store.uniform.dto

data class CreateOrderRequest(
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val zipCode: String,
    val orderItems: List<OrderItemRequest>
)
