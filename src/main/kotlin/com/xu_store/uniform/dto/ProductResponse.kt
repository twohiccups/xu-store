package com.xu_store.uniform.dto

import com.xu_store.uniform.model.Product
import com.xu_store.uniform.model.ProductVariation
import java.time.LocalDateTime


data class ProductsResponse(
    val products: List<ProductResponse>
)

// Main DTO for a single Product.
data class ProductResponse(
    val id: Long?,
    val name: String,
    val description: String?,
    val variations: List<ProductVariationResponse>,
    val productGroups: ProductGroupsResponse
) {
    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                id = product.id,
                name = product.name,
                description = product.description,
                variations = product.variations.map { ProductVariationResponse.from(it) },
                // Convert the groups (which is now a Collection) into our DTO.
                productGroups = ProductGroupsResponse.from(product.groups)
            )
        }
    }
}

// DTO for a Product Variation.
data class ProductVariationResponse(
    val id: Long? = null,
    val variationName: String,
    val price: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(productVariation: ProductVariation): ProductVariationResponse {
            return ProductVariationResponse(
                id = productVariation.id,
                variationName = productVariation.variationName,
                price = productVariation.price,
                createdAt = productVariation.createdAt,
                updatedAt = productVariation.updatedAt
            )
        }
    }
}
