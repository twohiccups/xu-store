package com.xu_store.uniform.dto

data class UserProductsResponse (
    val success: Boolean,
    val errorMessage: String?,
    val productResponse: List<ProductsResponse>
)