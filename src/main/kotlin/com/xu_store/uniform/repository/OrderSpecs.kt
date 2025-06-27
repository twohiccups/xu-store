package com.xu_store.uniform.repository

import com.xu_store.uniform.model.Order
import com.xu_store.uniform.model.OrderStatus
import org.springframework.data.jpa.domain.Specification

class OrderSpecs {

    fun withStatuses(orderStatuses: List<OrderStatus>): Specification<Order> =
        Specification { root, _, _ ->
            root.get<OrderStatus>("status").`in`(orderStatuses)
        }

    fun withTeamId(teamId: Long): Specification<Order> {
        return Specification { root, _, cb ->
            cb.equal(root.get<Long>("team").get<Long>("id"), teamId)
        }
    }
}
