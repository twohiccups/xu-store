package com.xu_store.uniform.repository

import com.xu_store.uniform.model.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository :
    JpaRepository<Order, Long>,
    JpaSpecificationExecutor<Order> {

    fun findAllByUserId(userId: Long): List<Order>
    fun findAllByTeamId(teamId: Long): List<Order>
}