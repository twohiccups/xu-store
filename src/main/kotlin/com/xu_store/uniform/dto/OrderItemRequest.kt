package com.xu_store.uniform.dto

data class OrderItemRequest(
    val productVariationId: Long,
    val quantity: Int,
    val unitPrice: Long
)
