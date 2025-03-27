package com.xu_store.uniform.dto

import com.xu_store.uniform.model.ProductGroup
import java.time.LocalDateTime

data class ProductGroupResponse(
    val id: Long?,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val productIds: List<Long> = emptyList(),
    val teamIds: List<Long> = emptyList()
) {
    companion object {
        fun from(productGroup: ProductGroup): ProductGroupResponse {
            return ProductGroupResponse(
                id = productGroup.id,
                name = productGroup.name,
                createdAt = productGroup.createdAt,
                updatedAt = productGroup.updatedAt,
                productIds = productGroup.productGroupAssignments.mapNotNull { it.product.id },
                teamIds = productGroup.teamProductGroups.mapNotNull { it.team.id }
            )
        }
    }
}
