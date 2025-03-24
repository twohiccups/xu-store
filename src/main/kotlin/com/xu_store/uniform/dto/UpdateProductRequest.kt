package com.xu_store.uniform.dto

data class UpdateProductRequest(
    val name: String,
    val description: String?,
    val productVariations: List<UpdateProductVariationRequest>
)
