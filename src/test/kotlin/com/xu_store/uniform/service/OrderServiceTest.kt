package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateOrderRequest
import com.xu_store.uniform.dto.OrderItemRequest
import com.xu_store.uniform.model.*
import com.xu_store.uniform.repository.OrderRepository
import com.xu_store.uniform.repository.ProductRepository
import com.xu_store.uniform.repository.UserRepository
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant

import java.util.Optional
import java.util.Optional.of
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OrderServiceTests {

    private val orderRepository = mock(OrderRepository::class.java)
    private val userRepository = mock(UserRepository::class.java)
    private val productRepository = mock(ProductRepository::class.java)
    private val orderService = OrderService(orderRepository, userRepository, productRepository)

    @Test
    fun `given valid order request, when placeOrder is called, then order is saved and user credits updated`() {
        // given: a user with sufficient store credits and a valid order request
        val team = Team(
            id = 1,
            name = "Besties",
            shippingFee = 10L  // shippingFee as Long
        )

        val user = User(
            id = 1,
            email = "user@example.com",
            passwordHash = "hash",
            storeCredits = 1000,
            team = team,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        // given: a valid order item request
        val orderItemRequest = OrderItemRequest(
            productVariationId = 10,
            quantity = 2,
            unitPrice = 100  // unitPrice as Long
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

        // given: product repository mocks (price and variation retrieval)
        whenever(productRepository.findVariationPrice(10))
            .thenReturn(of(100L))
        val product = Product(id = 10L, name = "Product Name")
        whenever(productRepository.getProductByProductVariations_Id(10))
            .thenReturn(product)
        val productVariation = ProductVariation(
            id = 10,
            price = 100,
            product = product,
            variationName = "Variation",
            displayOrder = 0,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        whenever(productRepository.findProductVariationById(10))
            .thenReturn(of(productVariation))

        // given: orderRepository simulates saving and assigns an id to the Order
        whenever(orderRepository.save(any<Order>()))
            .thenAnswer { invocation ->
                val orderArg = invocation.getArgument<Order>(0)
                orderArg.copy(id = 100)
            }

        // when: placing the order
        val placedOrder = orderService.placeOrder(request, user)

        // then: verify the order was saved and returned with an id
        assertEquals(100, placedOrder.id)

        // then: verify that the user's store credits have been updated
        // Calculation: Order total = 2 * 100L = 200L, so new credits = 1000L - 200L = 800L
        val userCaptor = argumentCaptor<User>()
        verify(userRepository).save(userCaptor.capture())
        assertEquals(800, userCaptor.firstValue.storeCredits)
        verify(orderRepository, times(1)).save(any<Order>())
    }

    @Test
    fun `given insufficient store credits, when placeOrder is called, then IllegalArgumentException is thrown`() {
        // given: a user with insufficient store credits
        val team = Team(
            id = 1,
            name = "Besties",
            shippingFee = 10
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

        // given: a valid order item request
        val orderItemRequest = OrderItemRequest(
            productVariationId = 10,
            quantity = 2,
            unitPrice = 100
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

        // given: product repository mocks similar to the valid scenario
        whenever(productRepository.findVariationPrice(10))
            .thenReturn(of(100))
        val product = Product(id = 10, name = "Product Name")
        whenever(productRepository.getProductByProductVariations_Id(10))
            .thenReturn(product)
        val productVariation = ProductVariation(
            id = 10,
            price = 100,
            product = product,
            variationName = "Variation",
            displayOrder = 0,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        whenever(productRepository.findProductVariationById(10))
            .thenReturn(of(productVariation))

        // when & then: calling placeOrder should throw an exception due to insufficient credits
        val ex = assertFailsWith<IllegalArgumentException> {
            orderService.placeOrder(request, user)
        }
        assertEquals("Not enough store credits", ex.message)

        verify(orderRepository, never()).save(any<Order>())
        verify(userRepository, never()).save(any<User>())
    }
}
