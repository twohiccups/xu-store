package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateOrderRequest
import com.xu_store.uniform.dto.OrderItemRequest
import com.xu_store.uniform.exception.InsufficientCreditsException
import com.xu_store.uniform.exception.InvalidProductVariationException
import com.xu_store.uniform.exception.UserWithoutTeamException
import com.xu_store.uniform.model.Order
import com.xu_store.uniform.model.OrderItem
import com.xu_store.uniform.model.OrderStatus
import com.xu_store.uniform.model.Product
import com.xu_store.uniform.model.ProductVariation
import com.xu_store.uniform.model.Team
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.OrderRepository
import com.xu_store.uniform.repository.ProductRepository
import com.xu_store.uniform.security.CustomUserDetails
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OrderServiceTest {

    private val orderRepository: OrderRepository = mock(OrderRepository::class.java)
    private val userService: UserService = mock(UserService::class.java)
    private val productRepository: ProductRepository = mock(ProductRepository::class.java)
    private val creditService: CreditService = mock(CreditService::class.java)

    private val service = OrderService(orderRepository, userService, productRepository, creditService)

    @Test
    fun `placeOrder persists order and logs credit transaction`() {
        val team = Team(id = 7L, name = "Falcons", shippingFee = 500L)
        val user = User(
            id = 12L,
            email = "ada@example.com",
            passwordHash = "hash",
            team = team,
            storeCredits = 10_000L,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val currentUser = CustomUserDetails(user)

        val product = Product(id = 55L, name = "Warmup Jacket")
        val variation = ProductVariation(
            id = 101L,
            product = product,
            variationName = "Medium",
            price = 2500L,
            displayOrder = 0,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val request = CreateOrderRequest(
            firstName = "Ada",
            lastName = "Lovelace",
            addressLine1 = "42 Binary Way",
            addressLine2 = null,
            city = "Seattle",
            state = "WA",
            zipCode = "98101",
            orderItems = listOf(
                OrderItemRequest(
                    productVariationId = requireNotNull(variation.id),
                    quantity = 2,
                    unitPrice = 999L // ignored by service in favor of db price
                )
            )
        )

        whenever(userService.getUserById(user.id!!)).thenReturn(user)
        whenever(productRepository.findVariationsWithProductByIdIn(setOf(101L))).thenReturn(listOf(variation))

        val savedOrderCaptor = argumentCaptor<Order>()
        whenever(orderRepository.save(savedOrderCaptor.capture())).thenAnswer { invocation ->
            val orderArg = invocation.getArgument<Order>(0)
            orderArg.copy(id = 444L)
        }

        val result = service.placeOrder(request, currentUser)

        val expectedTotal = 2 * 2500L + 500L
        assertEquals(444L, result.id)
        assertEquals(expectedTotal, result.totalAmount)
        assertEquals(OrderStatus.PENDING, result.status)

        val persistedOrder = savedOrderCaptor.firstValue
        assertEquals(expectedTotal, persistedOrder.totalAmount)
        assertEquals(team, persistedOrder.team)
        assertEquals(1, persistedOrder.orderItems.size)
        val persistedItem: OrderItem = persistedOrder.orderItems.first()
        assertEquals(variation.product, persistedItem.product)
        assertEquals(variation, persistedItem.productVariation)
        assertEquals(2, persistedItem.quantity)
        assertEquals(2500L, persistedItem.unitPrice)

        verify(userService, times(1)).deductUserCreditsOrThrow(user.id!!, expectedTotal)
        val logOrderCaptor = argumentCaptor<Order>()
        verify(creditService).logOrderTransaction(
            eq(user.id!!),
            eq(-expectedTotal),
            eq("Order #444"),
            logOrderCaptor.capture()
        )
        assertEquals(444L, logOrderCaptor.firstValue.id)
        verify(orderRepository, times(1)).save(any<Order>())
    }

    @Test
    fun `placeOrder propagates insufficient credits`() {
        val team = Team(id = 5L, name = "Owls", shippingFee = 1000L)
        val user = User(
            id = 13L,
            email = "no-credits@example.com",
            passwordHash = "hash",
            team = team,
            storeCredits = 1000L,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val request = CreateOrderRequest(
            firstName = "Low",
            lastName = "Credits",
            addressLine1 = "1 Penny Ln",
            addressLine2 = null,
            city = "Austin",
            state = "TX",
            zipCode = "73301",
            orderItems = listOf(
                OrderItemRequest(
                    productVariationId = 201L,
                    quantity = 1,
                    unitPrice = 1000L
                )
            )
        )
        val currentUser = CustomUserDetails(user)
        val product = Product(id = 99L, name = "Training Tee")
        val variation = ProductVariation(
            id = 201L,
            product = product,
            variationName = "Large",
            price = 5000L,
            displayOrder = 0,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        whenever(userService.getUserById(user.id!!)).thenReturn(user)
        whenever(productRepository.findVariationsWithProductByIdIn(setOf(201L))).thenReturn(listOf(variation))

        val orderTotal = 5000L + 1000L
        doThrow(InsufficientCreditsException("Not enough credits")).whenever(userService)
            .deductUserCreditsOrThrow(user.id!!, orderTotal)

        val ex = assertFailsWith<InsufficientCreditsException> {
            service.placeOrder(request, currentUser)
        }
        assertEquals("Not enough credits", ex.message)

        verify(orderRepository, never()).save(any<Order>())
        verify(creditService, never()).logOrderTransaction(any<Long>(), any<Long>(), any(), any<Order>())
    }

    @Test
    fun `placeOrder throws when user lacks a team`() {
        val user = User(
            id = 77L,
            email = "solo@example.com",
            passwordHash = "hash",
            team = null,
            storeCredits = 5000L,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val currentUser = CustomUserDetails(user)
        val request = CreateOrderRequest(
            firstName = "Solo",
            lastName = "Player",
            addressLine1 = "123 Alone St",
            addressLine2 = null,
            city = "Nowhere",
            state = "NA",
            zipCode = "00000",
            orderItems = listOf(
                OrderItemRequest(productVariationId = 11L, quantity = 1, unitPrice = 0L)
            )
        )

        whenever(userService.getUserById(user.id!!)).thenReturn(user)

        assertFailsWith<UserWithoutTeamException> {
            service.placeOrder(request, currentUser)
        }

        verify(productRepository, never()).findVariationsWithProductByIdIn(any<Set<Long>>())
        verify(orderRepository, never()).save(any<Order>())
    }

    @Test
    fun `placeOrder throws when variation is missing`() {
        val team = Team(id = 1L, name = "Eagles", shippingFee = 200L)
        val user = User(
            id = 88L,
            email = "missing@example.com",
            passwordHash = "hash",
            team = team,
            storeCredits = 10000L,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val request = CreateOrderRequest(
            firstName = "Missing",
            lastName = "Variation",
            addressLine1 = "123 Elm St",
            addressLine2 = null,
            city = "Denver",
            state = "CO",
            zipCode = "80202",
            orderItems = listOf(
                OrderItemRequest(productVariationId = 99L, quantity = 1, unitPrice = 0L)
            )
        )
        val currentUser = CustomUserDetails(user)

        whenever(userService.getUserById(user.id!!)).thenReturn(user)
        whenever(productRepository.findVariationsWithProductByIdIn(setOf(99L))).thenReturn(emptyList())

        assertFailsWith<InvalidProductVariationException> {
            service.placeOrder(request, currentUser)
        }

        verify(userService, never()).deductUserCreditsOrThrow(any<Long>(), any<Long>())
        verify(orderRepository, never()).save(any<Order>())
    }

    @Test
    fun `placeOrder rejects non positive quantity`() {
        val team = Team(id = 2L, name = "Bears", shippingFee = 300L)
        val user = User(
            id = 91L,
            email = "zero@example.com",
            passwordHash = "hash",
            team = team,
            storeCredits = 10000L,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val variation = ProductVariation(
            id = 321L,
            product = Product(id = 64L, name = "Cap"),
            variationName = "One Size",
            price = 1500L,
            displayOrder = 0,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val request = CreateOrderRequest(
            firstName = "Zero",
            lastName = "Quantity",
            addressLine1 = "5 Void Dr",
            addressLine2 = null,
            city = "Chicago",
            state = "IL",
            zipCode = "60601",
            orderItems = listOf(
                OrderItemRequest(productVariationId = 321L, quantity = 0, unitPrice = 0L)
            )
        )
        val currentUser = CustomUserDetails(user)

        whenever(userService.getUserById(user.id!!)).thenReturn(user)
        whenever(productRepository.findVariationsWithProductByIdIn(setOf(321L))).thenReturn(listOf(variation))

        assertFailsWith<IllegalArgumentException> {
            service.placeOrder(request, currentUser)
        }

        verify(userService, never()).deductUserCreditsOrThrow(any<Long>(), any<Long>())
        verify(orderRepository, never()).save(any<Order>())
    }
}
