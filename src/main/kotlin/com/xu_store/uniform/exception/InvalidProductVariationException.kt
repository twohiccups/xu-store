package com.xu_store.uniform.exception

class InvalidProductVariationException(val variationId: Long?, message: String? = "Invalid product variation $variationId") : RuntimeException(message)



