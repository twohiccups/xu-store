package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.CreateOrderRequest
import com.xu_store.uniform.dto.CreateOrderResponse
import com.xu_store.uniform.dto.OrderResponse
import com.xu_store.uniform.security.CustomUserDetails
import com.xu_store.uniform.service.OrderService
import com.xu_store.uniform.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService,
    private val userService: UserService
) {

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun placeOrder(
        @AuthenticationPrincipal currentUser: CustomUserDetails,
        @RequestBody request: CreateOrderRequest,
    ): ResponseEntity<CreateOrderResponse> {

        val user = userService.getUserByEmail(currentUser.username)
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    fun getMyOrders(@AuthenticationPrincipal currentUser: CustomUserDetails): ResponseEntity<List<OrderResponse>> {
        val user = userService.getUserByEmail(currentUser.username) ?: throw UsernameNotFoundException("User doesn't exist")
        val orders = orderService.getOrdersByUserId(requireNotNull(user.id))
        val response = orders.map { OrderResponse.from(it) }
        return ResponseEntity.ok(response)
    }
}
