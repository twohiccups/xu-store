package com.xu_store.uniform.controller


import com.xu_store.uniform.dto.CreateProductRequest
import com.xu_store.uniform.dto.UpdateProductRequest
import com.xu_store.uniform.dto.ProductResponse
import com.xu_store.uniform.repository.UserRepository
import com.xu_store.uniform.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")

class ProductController(
    private val productService: ProductService,
    private val userRepository: UserRepository
) {


    @GetMapping
    fun listAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.listAllProducts();
        return ResponseEntity.ok(products.map { ProductResponse.from(it) })
    }


    @PostMapping
    fun createProduct(@RequestBody request: CreateProductRequest): ResponseEntity<ProductResponse> {
        val product = productService.createProductWithVariations(request)
        return ResponseEntity.ok(ProductResponse.from(product))
    }



    // 4) Modify (update) a product and its variations
    @PutMapping("/{productId}")
    fun updateProduct(
        @PathVariable productId: Long,
        @RequestBody request: UpdateProductRequest
    ): ResponseEntity<ProductResponse> {
        val updatedProduct = productService.updateProduct(productId, request)
        return ResponseEntity.ok(ProductResponse.from(updatedProduct))
    }

    // 5) Delete a product
    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable productId: Long): ResponseEntity<Void> {
        productService.deleteProductById(productId)
        return ResponseEntity.noContent().build()
    }
}
