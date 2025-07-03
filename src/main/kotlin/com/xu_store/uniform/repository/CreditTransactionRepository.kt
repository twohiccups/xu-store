package com.xu_store.uniform.repository

import com.xu_store.uniform.model.CreditTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditTransactionRepository : JpaRepository<CreditTransaction, Long> {

    fun findAllByUserId(userId: Long): List<CreditTransaction>



}