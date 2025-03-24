package com.xu_store.uniform.controller


import com.example.demo.security.CustomUserDetails
import com.xu_store.uniform.dto.CreateProductRequest
import com.xu_store.uniform.dto.UpdateProductRequest
import com.xu_store.uniform.dto.ProductResponse
import com.xu_store.uniform.repository.UserRepository
import com.xu_store.uniform.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")

class ProductController(
    private val productService: ProductService,
    private val userRepository: UserRepository
) {

    // This endpoint is available for every authenticated user (any role)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current")
    fun listProductsForCurrentUser(): ResponseEntity<List<ProductResponse>> {
        // Obtain the currently authenticated user's username
        val authentication = SecurityContextHolder.getContext().authentication
        val username = (authentication.principal as CustomUserDetails).username

        // Assuming your service has a method to list products by user
        val products = productService.listProductsForUser(username)
        return ResponseEntity.ok(products.map { ProductResponse.from(it) })
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun listAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.listAllProducts();
        return ResponseEntity.ok(products.map { ProductResponse.from(it) })
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createProduct(@RequestBody request: CreateProductRequest): ResponseEntity<ProductResponse> {
        val product = productService.createProductWithVariations(request)
        return ResponseEntity.ok(ProductResponse.from(product))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    fun updateProduct(
        @PathVariable productId: Long,
        @RequestBody request: UpdateProductRequest
    ): ResponseEntity<ProductResponse> {
        val updatedProduct = productService.updateProduct(productId, request)
        return ResponseEntity.ok(ProductResponse.from(updatedProduct))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable productId: Long): ResponseEntity<Void> {
        productService.deleteProductById(productId)
        return ResponseEntity.noContent().build()
    }

}
