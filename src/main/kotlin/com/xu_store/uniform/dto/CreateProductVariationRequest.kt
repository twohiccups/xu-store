package com.xu_store.uniform.dto

data class CreateProductVariationRequest (
    val variationName: String,
    val displayOrder: Int,
    val price: Long
)
