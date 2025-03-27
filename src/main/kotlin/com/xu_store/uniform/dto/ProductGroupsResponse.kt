package com.xu_store.uniform.dto

import com.xu_store.uniform.model.ProductGroup

// DTO for a collection of Product Groups.
data class ProductGroupsResponse(
    val productGroups: List<ProductGroupResponse>
) {
    companion object {
        fun from(groups: Collection<ProductGroup>): ProductGroupsResponse {
            return ProductGroupsResponse(
                productGroups = groups.map { ProductGroupResponse.from(it) }
            )
        }
    }
}