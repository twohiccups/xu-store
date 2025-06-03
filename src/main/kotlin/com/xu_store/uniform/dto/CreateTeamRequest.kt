package com.xu_store.uniform.dto

import java.time.Instant


data class CreateTeamRequest(
    val name: String,
    val shippingFee: Long,
    val createdAt: Instant
? = null,
    val updatedAt: Instant
? = null
)
