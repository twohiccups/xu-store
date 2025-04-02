package com.xu_store.uniform.dto

import com.xu_store.uniform.model.OrderItem

data class OrderItemResponse (
    val productId: Long,
    val productVariationId: Long,
    val quantity: Int,
    val unitPrice: Long
) {

    companion object {
        fun from(orderItem: OrderItem): OrderItemResponse {
            return OrderItemResponse(
                productId = requireNotNull(orderItem.product.id),
                productVariationId = requireNotNull(orderItem.productVariation.id),
                quantity = orderItem.quantity,
                unitPrice = orderItem.unitPrice
            )
        }
    }
}
