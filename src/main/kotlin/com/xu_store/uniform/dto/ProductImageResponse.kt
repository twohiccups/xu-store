package com.xu_store.uniform.dto

import com.xu_store.uniform.model.ProductImage

data class ProductImageResponse (
    val id: Long,
    val productId: Long,
    val imageUrl: String,
) {
    companion object {
        fun from(productImage: ProductImage) : ProductImageResponse{
            return ProductImageResponse(
                id = requireNotNull(productImage.id),
                productId = requireNotNull(productImage.product.id),
                imageUrl = productImage.imageUrl
            )
        }
    }
}