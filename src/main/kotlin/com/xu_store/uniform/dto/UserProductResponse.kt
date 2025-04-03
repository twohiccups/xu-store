package com.xu_store.uniform.dto

import java.lang.Error

data class UserProductsResponse (
    val success: Boolean,
    val errorMessage: String?,
    val productResponse: List<ProductsResponse>
)