package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.*
import com.xu_store.uniform.model.OrderStatus
import com.xu_store.uniform.security.CustomUserDetails
import com.xu_store.uniform.service.OrderService
import com.xu_store.uniform.service.UserService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(createOrderResponse)
        }
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
