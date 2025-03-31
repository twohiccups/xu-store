package com.xu_store.uniform.dto

data class CreateProductRequest(
    val name: String,
    val description: String,
    val productVariations: List<CreateProductVariationRequest>
)

