package com.xu_store.uniform.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class CreateOrderRequest(
    @field:NotBlank
    val firstName: String,

    @field:NotBlank
    val lastName: String,

    @field:NotBlank
    val addressLine1: String,

    val addressLine2: String?,

    @field:NotBlank
    val city: String,

    @field:NotBlank
    val state: String,

    @field:NotBlank
    val zipCode: String,

    @field:NotEmpty(message = "orderItems cannot be empty")
    @field:Valid
    val orderItems: List<OrderItemRequest>
)
