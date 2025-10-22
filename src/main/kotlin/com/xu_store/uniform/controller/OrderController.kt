package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.*
import com.xu_store.uniform.exception.InsufficientCreditsException
import com.xu_store.uniform.model.OrderStatus
import com.xu_store.uniform.security.CustomUserDetails
import com.xu_store.uniform.service.OrderService
import com.xu_store.uniform.service.UserService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

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
        @RequestBody @Valid request: CreateOrderRequest,
    ): ResponseEntity<OrderResponse> {
        val order = orderService.placeOrder(request, currentUser)
        val location = URI.create("/api/orders/${order.id}")
        return ResponseEntity.created(location).body(OrderResponse.from(order))
    }

    @GetMapping
    fun findOrders(
        @RequestParam(required = false) statuses: List<OrderStatus>?,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) teamId: Long?
    ): Page<OrderResponse> {
        return orderService.listOrders(
            statuses = statuses,
            page = page ?: 0,
            pageSize = size,
            teamId = teamId
        ).map(OrderResponse::from)
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody req: UpdateOrderStatusRequest
    ): ResponseEntity<OrderResponse> {
        val updated = orderService.updateOrderStatus(id, req.status)
        return ResponseEntity.ok(OrderResponse.from(updated))
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    fun getMyOrders(@AuthenticationPrincipal currentUser: CustomUserDetails): ResponseEntity<List<OrderResponse>> {
        val user = userService.getUserByEmail(currentUser.username)
        val orders = orderService.getOrdersByUserId(requireNotNull(user.id))
        val response = orders.map { OrderResponse.from(it) }
        return ResponseEntity.ok(response)
    }
}
