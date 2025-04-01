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

        val totalAmount = request.orderItems.sumOf {
            it.quantity *  productRepository.findVariationPrice(it.productVariationId).get()
        }

        val difference = user.storeCredits - totalAmount
        require(difference >= 0) {"Not enough store credits"}

        val userCopy = user.copy(storeCredits = difference)

        val order = Order(
            user = userCopy,
            team = userCopy.team,  // if user belongs to a team
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

        return orderRepository.save(order)
    }

    fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }

    fun getOrdersByUser(userId: Long): List<Order> {
        return orderRepository.findAllByUserId(userId)
    }
}
