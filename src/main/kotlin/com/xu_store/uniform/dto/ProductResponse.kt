package com.xu_store.uniform.dto

import com.xu_store.uniform.model.Product
import com.xu_store.uniform.model.ProductVariation
import java.time.LocalDateTime


data class ProductsResponse(
    val products: List<ProductResponse>
)

// Main DTO for a single Product.
data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val productVariations: List<ProductVariationResponse>,
    val productImages: List<ProductImageResponse>,
    val productGroups: ProductGroupsResponse
) {
    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                id = requireNotNull(product.id) { "Product ID must not be null"},
                name = product.name,
                description = product.description,
                productVariations = product.productVariations.map { ProductVariationResponse.from(it) },
                productImages = product.images.map {image -> ProductImageResponse.from(image)},
                productGroups = ProductGroupsResponse.from(product.groups)
            )
        }
    }
}

// DTO for a Product Variation.
data class ProductVariationResponse(
    val id: Long,
    val variationName: String,
    val price: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(productVariation: ProductVariation): ProductVariationResponse {
            return ProductVariationResponse(
                id = requireNotNull(productVariation.id) {"Product Variation Id must not be null"},
                variationName = productVariation.variationName,
                price = productVariation.price,
                createdAt = productVariation.createdAt,
                updatedAt = productVariation.updatedAt
            )
        }
    }
}
