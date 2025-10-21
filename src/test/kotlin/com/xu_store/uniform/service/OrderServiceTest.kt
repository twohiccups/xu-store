package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateOrderRequest
import com.xu_store.uniform.dto.OrderItemRequest
import com.xu_store.uniform.exception.NotEnoughCreditsException
import com.xu_store.uniform.model.Order
import com.xu_store.uniform.model.Product
import com.xu_store.uniform.model.ProductVariation
import com.xu_store.uniform.model.Team
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.OrderRepository
import com.xu_store.uniform.repository.ProductRepository
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant

import java.util.Optional.of
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OrderServiceTests {

    private val orderRepository = mock(OrderRepository::class.java)
    private val userService = mock(UserService::class.java)
    private val productRepository = mock(ProductRepository::class.java)
    private val creditService = mock(CreditService::class.java)
    private val orderService = OrderService(orderRepository, userService, productRepository, creditService)

    @Test
    fun `given valid order request, when placeOrder is called, then order is saved and user credits updated`() {
        // given: a user with sufficient store credits and a valid order request
        val team = Team(
            id = 1,
            name = "Besties",
            shippingFee = 10L
        )

        val user = User(
            id = 1L,
            email = "user@example.com",
            passwordHash = "hash",
            storeCredits = 1000,
            team = team,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val userId = requireNotNull(user.id)

        // given: a valid order item request
        val orderItemRequest = OrderItemRequest(
            productVariationId = 10L,
            quantity = 2,
            unitPrice = 100L
        )
        val request = CreateOrderRequest(
            orderItems = listOf(orderItemRequest),
            firstName = "John",
            lastName = "Doe",
            addressLine1 = "123 Main St",
            addressLine2 = "",
            city = "City",
            state = "State",
            zipCode = "12345"
        )

        whenever(userService.getUserById(userId))
            .thenReturn(user)

        // given: product repository mocks (price and variation retrieval)
        whenever(productRepository.findVariationPrice(10L))
            .thenReturn(of(100L))
        val product = Product(id = 10L, name = "Product Name")
        whenever(productRepository.getProductByProductVariations_Id(10L))
            .thenReturn(product)
        val productVariation = ProductVariation(
            id = 10L,
            price = 100L,
            product = product,
            variationName = "Variation",
            displayOrder = 0,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        whenever(productRepository.findProductVariationById(10L))
            .thenReturn(of(productVariation))

        // given: orderRepository simulates saving and assigns an id to the Order
        val orderCaptor = argumentCaptor<Order>()
        whenever(orderRepository.save(orderCaptor.capture())).thenAnswer { invocation ->
            val orderArg = invocation.getArgument<Order>(0)
            orderArg.copy(id = 100L)
        }

        // when: placing the order
        val placedOrder = orderService.placeOrder(request, userId)

        // then: verify the order was saved and returned with an id
        assertEquals(100L, placedOrder.id)
        assertEquals(210L, orderCaptor.firstValue.totalAmount)

        // then: verify that the user's store credits have been updated
        verify(userService).deductUserCreditsOrThrow(userId, 210L)
        verify(creditService).logOrderTransaction(userId, -210L, "Order #100", placedOrder)
        verify(orderRepository, times(1)).save(any<Order>())
    }

    @Test
    fun `given insufficient store credits, when placeOrder is called, then NotEnoughCreditsException is thrown`() {
        // given: a user with insufficient store credits
        val team = Team(
            id = 1,
            name = "Besties",
            shippingFee = 10L
        )
        val user = User(
            id = 1L,
            email = "user@example.com",
            passwordHash = "hash",
            storeCredits = 150,  // Too low for the order
            team = team,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val userId = requireNotNull(user.id)

        // given: a valid order item request
        val orderItemRequest = OrderItemRequest(
            productVariationId = 10L,
            quantity = 2,
            unitPrice = 100L
        )
        val request = CreateOrderRequest(
            orderItems = listOf(orderItemRequest),
            firstName = "John",
            lastName = "Doe",
            addressLine1 = "123 Main St",
            addressLine2 = "",
            city = "City",
            state = "State",
            zipCode = "12345"
        )

        whenever(userService.getUserById(userId))
            .thenReturn(user)

        // given: product repository mocks similar to the valid scenario
        whenever(productRepository.findVariationPrice(10L))
            .thenReturn(of(100L))
        val product = Product(id = 10L, name = "Product Name")
        whenever(productRepository.getProductByProductVariations_Id(10L))
            .thenReturn(product)
        val productVariation = ProductVariation(
            id = 10L,
            price = 100L,
            product = product,
            variationName = "Variation",
            displayOrder = 0,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        whenever(productRepository.findProductVariationById(10L))
            .thenReturn(of(productVariation))

        val totalAmount = 210L
        doThrow(NotEnoughCreditsException("User $userId has insufficient store credits to deduct $totalAmount"))
            .whenever(userService).deductUserCreditsOrThrow(eq(userId), eq(totalAmount))

        // when & then: calling placeOrder should throw an exception due to insufficient credits
        val ex = assertFailsWith<NotEnoughCreditsException> {
            orderService.placeOrder(request, userId)
        }
        assertEquals("User $userId has insufficient store credits to deduct $totalAmount", ex.message)

        verify(orderRepository, never()).save(any<Order>())
        verify(userService).deductUserCreditsOrThrow(userId, totalAmount)
        verify(creditService, never()).logOrderTransaction(any(), any(), any(), any())
    }
}
