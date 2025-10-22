package com.xu_store.uniform.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero

data class OrderItemRequest(

    @field:NotNull
    val productVariationId: Long,

    @field:Positive
    val quantity: Int,

    @field:PositiveOrZero // In case of free extras
    val unitPrice: Long
)
