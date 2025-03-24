package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateOrderRequest
import com.xu_store.uniform.dto.OrderItemRequest
import com.xu_store.uniform.model.Order
import com.xu_store.uniform.model.OrderStatus
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.OrderRepository
import com.xu_store.uniform.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository // For validation; assume we check allowed items
) {

    @Transactional
    fun placeOrder(request: CreateOrderRequest, user: User): Order {
        // Validate order items, ensuring that the user is allowed to order them.
        // (This validation logic is omitted for brevity.)

        // Compute total amount.
        val totalAmount = request.orderItems.sumOf { it.quantity * it.unitPrice.toLong() }

        // Create the order.
        val order = Order(
            user = user,
            team = user.team,  // if user belongs to a team
            totalAmount = totalAmount,
            status = OrderStatus.PENDING,
            addressLine1 = request.addressLine1,
            addressLine2 = request.addressLine2,
            city = request.city,
            state = request.state,
            zipCode = request.zipCode,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // In a complete implementation, you would also map each OrderItemRequest to an OrderItem entity
        // and add them to order.orderItems. For brevity, that part is omitted.

        return orderRepository.save(order)
    }

    fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }

    fun getOrdersByUser(userId: Long): List<Order> {
        return orderRepository.findAllByUserId(userId)
    }
}
