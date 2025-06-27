// src/main/kotlin/com/xu_store/uniform/dto/ChangeOrderStatusRequest.kt
package com.xu_store.uniform.dto

import com.xu_store.uniform.model.OrderStatus


data class UpdateOrderStatusRequest(
    val status: OrderStatus
)

