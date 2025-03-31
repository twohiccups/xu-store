package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.CreateCreditTransactionResponse
import com.xu_store.uniform.dto.CreditTransactionRequest
import com.xu_store.uniform.dto.CreditTransactionResponse
import com.xu_store.uniform.service.CreditService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/credit")
class CreditController (
    private val creditService: CreditService
) {

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("getStoreCreditHistory")
//    fun getCreditTransactionHistoryByUserId(userId: Long) : CreditTransactionResponse {
//        return creditService.getCreditTransactionHistoryByUserId(userId)
//    }

    @PostMapping("createCreditTransaction")
    @PreAuthorize("hasRole('ADMIN')")
    fun createCreditTransaction(@RequestBody creditTransactionRequest: CreditTransactionRequest) : CreateCreditTransactionResponse {
        try {
            val creditTransaction = creditService.processCreditTransactionRequest(creditTransactionRequest)
            return CreateCreditTransactionResponse(
                success = true,
                creditTransaction = CreditTransactionResponse.from(creditTransaction)
            )
        } catch (e: Exception) {
            return CreateCreditTransactionResponse(
                success = false,
                error = e.message
            )
        }
    }
}