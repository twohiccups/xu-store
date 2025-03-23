package com.xu_store.uniform.dto

import com.xu_store.uniform.model.Product
import com.xu_store.uniform.model.ProductVariation
import jakarta.persistence.*
import java.time.LocalDateTime

data class ProductsResponse (
    val products: List<ProductResponse>
)

data class ProductResponse (
    val id: Long?,
    val name: String,
    val description: String?,
    val variations: List<ProductVariationResponse>
) {
    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                id = product.id,
                name = product.name,
                description = product.description,
                variations = product.variations.map { ProductVariationResponse.from(it) }
            )
        }
    }

    data class ProductVariationResponse(
        val id: Long? = null,
        val variationName: String,
        val price: Long,
        var createdAt: LocalDateTime,
        var updatedAt: LocalDateTime,
    ) {
        companion object {
            fun from(productVariation: ProductVariation): ProductVariationResponse {
                return ProductVariationResponse(
                    id = productVariation.id,
                    variationName = productVariation.variationName,
                    price = productVariation.price,
                    createdAt = productVariation.createdAt,
                    updatedAt = productVariation.updatedAt,
                )
            }
        }
    }
}
