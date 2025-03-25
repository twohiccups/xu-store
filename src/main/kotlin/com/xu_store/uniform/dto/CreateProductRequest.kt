package com.xu_store.uniform.dto

import com.xu_store.uniform.dto.CreateProductVariationRequest

data class CreateProductRequest(
    val name: String,
    val description: String,
    val productVariations: List<CreateProductVariationRequest>
)

