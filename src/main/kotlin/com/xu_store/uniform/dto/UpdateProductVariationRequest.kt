package com.xu_store.uniform.dto


data class UpdateProductVariationRequest(
    val id: Long?,
    val variationName: String,
    val price: Long
)