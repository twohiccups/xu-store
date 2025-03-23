package com.xu_store.uniform.dto


data class UpdateProductVariationRequest(
    // If null, this variation is new; if non-null, it should match an existing variation ID.
    val id: Long?,
    val variationName: String,
    val price: Long
)