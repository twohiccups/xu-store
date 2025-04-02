package com.xu_store.uniform.dto

import java.time.LocalDateTime

data class CreateTeamRequest(
    val name: String,
    val shippingFee: Long,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
