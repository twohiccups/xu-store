package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateOrderRequest
import com.xu_store.uniform.model.Order
import com.xu_store.uniform.model.OrderItem
import com.xu_store.uniform.model.OrderStatus
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.OrderRepository
import com.xu_store.uniform.repository.ProductRepository
import com.xu_store.uniform.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository // For validation; assume we check allowed items
) {

    @Transactional
    fun placeOrder(request: CreateOrderRequest, user: User): Order {

        val totalAmount = request.orderItems.sumOf {
            it.quantity * productRepository.findVariationPrice(it.productVariationId).get()
        }

        val difference = user.storeCredits - totalAmount
        require(difference >= 0) { "Not enough store credits" }

        // Create the order without order items first
        val order = Order(
            user = user,
            team = user.team,
            shippingFee = requireNotNull(user.team?.shippingFee),
            totalAmount = totalAmount,
            status = OrderStatus.PENDING,
            firstName = request.firstName,
            lastName = request.lastName,
            addressLine1 = request.addressLine1,
            addressLine2 = request.addressLine2,
            city = request.city,
            state = request.state,
            zipCode = request.zipCode,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        // Build OrderItems with the new order reference
        val newOrderItems = request.orderItems.map { orderItemRequest ->
            val product = productRepository.getProductByProductVariations_Id(orderItemRequest.productVariationId)
            val productVariation = productRepository.findProductVariationById(orderItemRequest.productVariationId).get()
            OrderItem(
                order = order, // Use the unsaved order; cascade will take care of it
                product = product,
                productVariation = productVariation,
                quantity = orderItemRequest.quantity,
                unitPrice = productVariation.price,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        }

        // Add the OrderItems to the Order
        order.orderItems.addAll(newOrderItems)

        // Save the order (cascading will persist the order items)
        val processedOrder = orderRepository.save(order)

        // Update user store credits
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

    fun getOrdersByUserId(userId: Long): List<Order> {
        return orderRepository.findAllByUserId(userId)
    }
}
