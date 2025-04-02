package com.xu_store.uniform.dto

import com.xu_store.uniform.model.OrderItem

data class OrderItemRequest(
    val productVariationId: Long,
    val quantity: Int,
    val unitPrice: Long
)
