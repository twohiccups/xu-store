package com.xu_store.uniform.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.xu_store.uniform.model.OrderStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FindOrdersRequest(
    val statuses: List<OrderStatus>? = null,
    val page: Int? = null,
    val size: Int? = null,
    val teamId: Long? = null  // ✅ Add this to filter by team
)