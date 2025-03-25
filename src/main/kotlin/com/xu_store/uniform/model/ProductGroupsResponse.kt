package com.xu_store.uniform.model

import java.time.LocalDateTime

data class ProductGroupsResponse(
    val productGroups: List<ProductGroupResponse>
) {
    companion object {
        fun from(productGroups: List<ProductGroup>): ProductGroupsResponse {
            return ProductGroupsResponse(
                productGroups = productGroups.map { group -> ProductGroupResponse.from(group)
            })
        }
    }
}