package com.xu_store.uniform.dto

import com.xu_store.uniform.model.ProductGroup
import java.time.Instant


data class ProductGroupResponse(
    val id: Long,
    val name: String,
    val createdAt: Instant
,
    val updatedAt: Instant
,
    val productIds: List<Long> = emptyList(),
    val teamIds: List<Long> = emptyList()
) {
    companion object {
        fun from(productGroup: ProductGroup): ProductGroupResponse {
            return ProductGroupResponse(
                id = requireNotNull(productGroup.id) { "Product Group Id must not be null"},
                name = productGroup.name,
                createdAt = productGroup.createdAt,
                updatedAt = productGroup.updatedAt,
                productIds = productGroup.productGroupAssignments.mapNotNull { it.product.id },
                teamIds = productGroup.teamProductGroups.mapNotNull { it.team.id }
            )
        }
    }
}
