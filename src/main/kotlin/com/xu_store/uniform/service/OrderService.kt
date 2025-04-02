package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateOrderRequest
import com.xu_store.uniform.dto.OrderItemRequest
import com.xu_store.uniform.model.Order
import com.xu_store.uniform.model.OrderStatus
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.OrderRepository
import com.xu_store.uniform.repository.ProductRepository
import com.xu_store.uniform.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository // For validation; assume we check allowed items
) {

    @Transactional
    fun placeOrder(request: CreateOrderRequest, user: User): Order {

        val totalAmount = request.orderItems.sumOf {
            it.quantity *  productRepository.findVariationPrice(it.productVariationId).get()
        }

        val difference = user.storeCredits - totalAmount
        require(difference >= 0) {"Not enough store credits"}


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
        val processedOrder = orderRepository.save(order)
        val userCopy = user.copy(storeCredits = difference)
        userRepository.save(userCopy)
        return processedOrder
    }

    fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }

    fun getAllOrdersByTeam(teamId: Long): List<Order> {
        return orderRepository.findAllByTeamId(teamId)
    }

    fun getOrdersByUser(userId: Long): List<Order> {
        return orderRepository.findAllByUserId(userId)
    }
}
