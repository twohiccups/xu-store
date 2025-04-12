package com.xu_store.uniform.dto

import com.xu_store.uniform.model.Order
import com.xu_store.uniform.model.OrderStatus
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val orderItems: List<OrderItemResponse>,
    val totalAmount: Long,
    val status: OrderStatus,
    val firstName: String,
    val lastName: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val zipCode: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(order: Order): OrderResponse {
            return OrderResponse(
                id = requireNotNull(order.id) { "Order Id must not be null"},
                userId = order.user.id ?: 0L,
                orderItems = order.orderItems.map { orderItem -> OrderItemResponse.from(orderItem = orderItem)},
                totalAmount = order.totalAmount,
                status = order.status,
                firstName = order.firstName,
                lastName = order.lastName,
                addressLine1 = order.addressLine1,
                addressLine2 = order.addressLine2,
                city = order.city,
                state = order.state,
                zipCode = order.zipCode,
                createdAt = order.createdAt,
                updatedAt = order.updatedAt
            )
        }
    }
}
