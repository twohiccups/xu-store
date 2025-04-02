package com.xu_store.uniform.controller

import com.example.demo.security.CustomUserDetails
import com.xu_store.uniform.dto.CreateOrderRequest
import com.xu_store.uniform.dto.CreateOrderResponse
import com.xu_store.uniform.dto.OrderResponse
import com.xu_store.uniform.dto.ProductResponse
import com.xu_store.uniform.model.User
import com.xu_store.uniform.service.OrderService
import com.xu_store.uniform.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService,
    private val userService: UserService
) {


    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun placeOrder(
        @RequestBody request: CreateOrderRequest,
    ): ResponseEntity<CreateOrderResponse> {
        // In production, you should retrieve the full User entity from your security context.
        // Here we simulate by creating a stub user.
        val authentication = SecurityContextHolder.getContext().authentication
        val email = (authentication.principal as CustomUserDetails).username

        val user = userService.getUserByEmail(email)
        requireNotNull(user) { "User is not found"}
        try {
            val order = orderService.placeOrder(request, user)
            val createOrderResponse = CreateOrderResponse(
                success = true,
                errorMessage = null,
                orderResponse = OrderResponse.from(order)
            )
            return ResponseEntity.status(HttpStatus.CREATED).body(createOrderResponse)

        } catch ( e: IllegalArgumentException) {
            val createOrderResponse = CreateOrderResponse(
                success = false,
                errorMessage = e.message,
                orderResponse = null
            )
            return ResponseEntity.status(HttpStatus.CREATED).body(createOrderResponse)
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getAllOrders(): ResponseEntity<List<OrderResponse>> {
        val orders = orderService.getAllOrders()
        val response = orders.map { OrderResponse.from(it) }
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/team/{teamId}")
    fun getAllOrdersByTeam(@PathVariable teamId: Long): ResponseEntity<List<OrderResponse>> {
        val orders = orderService.getAllOrdersByTeam(teamId)
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
