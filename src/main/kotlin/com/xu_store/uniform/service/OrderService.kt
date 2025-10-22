package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateOrderRequest
import com.xu_store.uniform.dto.CreditTransactionRequest
import com.xu_store.uniform.exception.InvalidProductVariationException
import com.xu_store.uniform.exception.UserWithoutTeamException
import com.xu_store.uniform.model.*
import com.xu_store.uniform.repository.OrderRepository
import com.xu_store.uniform.repository.OrderSpecs
import com.xu_store.uniform.repository.ProductRepository
import com.xu_store.uniform.repository.UserRepository
import com.xu_store.uniform.security.CustomUserDetails
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
    private val creditService: CreditService
) {

    @Transactional
    fun placeOrder(request: CreateOrderRequest, currentUser: CustomUserDetails): Order {
        val userId = currentUser.userId
        val user = userService.getUserById(userId)

        // Users are tethered to teams, where shipping fees are fixed per team
        val team = user.team ?: throw UserWithoutTeamException(userId)

        val variationIds: Set<Long> = request.orderItems.map { it.productVariationId }.toSet()
        require(variationIds.isNotEmpty()) { "orderItems cannot be empty" }

        val productVariations: List<ProductVariation> = productRepository.findVariationsWithProductByIdIn(variationIds)
        val productVariationsMap: Map<Long, ProductVariation> = productVariations.associateBy { requireNotNull(it.id) }

        if (variationIds.size != productVariationsMap.size) {
            // Some requested items are not in the db
            val missing: Set<Long> = variationIds - productVariationsMap.keys
            throw InvalidProductVariationException(missing.first())
        }

        val itemTotal = request.orderItems.sumOf {
            require(it.quantity > 0) {"Item quantity must be greater than zero"}
            it.quantity.toLong() * requireNotNull(productVariationsMap[it.productVariationId]).price
        }

        val shippingFee = team.shippingFee
        val totalAmount = itemTotal + shippingFee

        // Attempt credit deduction before creating order
        userService.deductUserCreditsOrThrow(userId, totalAmount)

        // Build and save order only if deduction succeeds:
        val now = Instant.now()
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
            createdAt = now,
            updatedAt = now
        )

        val orderItems = request.orderItems.map { item ->
            val productVariation = productVariationsMap.getValue(item.productVariationId)
            OrderItem(
                order = order,
                product = productVariation.product,
                productVariation = productVariation,
                quantity = item.quantity,
                unitPrice = productVariation.price,
                createdAt = now,
                updatedAt = now
            )
        }
        order.orderItems.addAll(orderItems)
        val savedOrder = orderRepository.save(order)

        // By this point order exists, now we can log the transaction
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
