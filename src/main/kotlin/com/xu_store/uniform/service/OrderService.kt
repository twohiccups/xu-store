package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateOrderRequest
import com.xu_store.uniform.dto.CreditTransactionRequest
import com.xu_store.uniform.model.Order
import com.xu_store.uniform.model.OrderItem
import com.xu_store.uniform.model.OrderStatus
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.OrderRepository
import com.xu_store.uniform.repository.OrderSpecs
import com.xu_store.uniform.repository.ProductRepository
import com.xu_store.uniform.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val userService: UserService,
    private val productRepository: ProductRepository,
    private val creditService: CreditService // ✅ Injected
) {

    @Transactional(rollbackFor = [Exception::class])
    fun placeOrder(request: CreateOrderRequest, userId: Long): Order {
        val user = userService.getUserById(userId)
        val team = user.team ?: throw IllegalStateException("User does not belong to a team")
        val shippingFee = team.shippingFee

        val itemTotal = request.orderItems.sumOf {
            val price = productRepository.findVariationPrice(it.productVariationId)
                .orElseThrow { IllegalArgumentException("Invalid product variation: ${it.productVariationId}") }
            it.quantity * price
        }

        val totalAmount = itemTotal + shippingFee

        // ✅ Attempt credit deduction BEFORE creating order
        userService.deductUserCreditsOrThrow(userId, totalAmount)

        // ✅ Only after deduction succeeds: build & save order
        val order = Order(
            user = user,
            team = team,
            shippingFee = shippingFee,
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

        val orderItems = request.orderItems.map { item ->
            val product = productRepository.getProductByProductVariations_Id(item.productVariationId)
            val variation = productRepository.findProductVariationById(item.productVariationId)
                .orElseThrow { IllegalArgumentException("Invalid product variation: ${item.productVariationId}") }

            OrderItem(
                order = order,
                product = product,
                productVariation = variation,
                quantity = item.quantity,
                unitPrice = variation.price,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        }
        order.orderItems.addAll(orderItems)
        val savedOrder = orderRepository.save(order)

        // ✅ Log the transaction after order is created
        creditService.logOrderTransaction(userId, -totalAmount, "Order #${savedOrder.id}", savedOrder)

        return savedOrder
    }




    @Transactional
    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus): Order {
        val order = orderRepository.findById(orderId)
            .orElseThrow { EntityNotFoundException("Order with id $orderId not found") }

        val orderCopy = order.copy(
            status = newStatus,
            updatedAt = Instant.now()
        )
        return orderRepository.save(orderCopy)
    }

    fun listOrders(
        statuses: List<OrderStatus>?,
        page: Int,
        pageSize: Int?,
        teamId: Long?
    ): Page<Order> {
        var spec: Specification<Order> = Specification.where(null)

        if (!statuses.isNullOrEmpty()) {
            spec = spec.and(OrderSpecs().withStatuses(statuses))
        }

        if (teamId != null) {
            spec = spec.and(OrderSpecs().withTeamId(teamId))
        }

        val size = pageSize ?: Int.MAX_VALUE
        val pageable = PageRequest.of(page, size, Sort.by("id").ascending())

        return orderRepository.findAll(spec, pageable)
    }

    fun getOrdersByUserId(userId: Long): List<Order> {
        return orderRepository.findAllByUserId(userId)
    }
}
