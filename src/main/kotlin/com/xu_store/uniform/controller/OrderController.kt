package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.CreateOrderRequest
import com.xu_store.uniform.dto.OrderResponse
import com.xu_store.uniform.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {

    // Endpoint for a user to place an order.
    @PostMapping
    fun placeOrder(
        @RequestBody request: CreateOrderRequest,
        principal: Principal  // Assume Principal.name gives the user's email
    ): ResponseEntity<OrderResponse> {
        // In production, you should retrieve the full User entity from your security context.
        // Here we simulate by creating a stub user.
        val user = com.xu_store.uniform.model.User(
            id = 1L,
            email = principal.name,
            passwordHash = "",
            role = "USER",
            storeCredits = 0,
            team = null,
            createdAt = java.time.LocalDateTime.now(),
            updatedAt = java.time.LocalDateTime.now()
        )
        val order = orderService.placeOrder(request, user)
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order))
    }

    // Endpoint for admin to view all orders.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getAllOrders(): ResponseEntity<List<OrderResponse>> {
        val orders = orderService.getAllOrders()
        val response = orders.map { OrderResponse.from(it) }
        return ResponseEntity.ok(response)
    }

    // Optional: Endpoint for a user to view their own orders.
    @GetMapping("/my")
    fun getMyOrders(principal: Principal): ResponseEntity<List<OrderResponse>> {
        // In production, retrieve the actual user's ID from the security context.
        val userId = 1L  // Stub value.
        val orders = orderService.getOrdersByUser(userId)
        val response = orders.map { OrderResponse.from(it) }
        return ResponseEntity.ok(response)
    }
}
