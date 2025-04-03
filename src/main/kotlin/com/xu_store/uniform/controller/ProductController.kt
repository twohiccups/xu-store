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
    fun getProductsForCurrentUser(): ResponseEntity<List<ProductResponse>> {
        // Obtain the currently authenticated user's username
        val authentication = SecurityContextHolder.getContext().authentication
        val username = (authentication.principal as CustomUserDetails).username
        val products = productService.getProductsForUser(username)
        // TODO(USE USERPRODUCTRESPONSE)
        return ResponseEntity.ok(products.map { ProductResponse.from(it) })
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{productId}")
    fun getProductById(
        @PathVariable productId: Long,
    ): ResponseEntity<ProductResponse> {
        val productOpt = productService.getProductById(productId)
        return if (productOpt.isPresent) {
            ResponseEntity.ok(ProductResponse.from(productOpt.get()))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getAllProducts();
        return ResponseEntity.ok(products.map { ProductResponse.from(it) })
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/archived")
    fun getAllArchivedProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getAllArchivedProducts();
        return ResponseEntity.ok(products.map { ProductResponse.from(it) })
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createProduct(@RequestBody createProductRequest: CreateProductRequest): ResponseEntity<ProductResponse> {
        val product = productService.createProductWithVariations(createProductRequest)
        return ResponseEntity.ok(ProductResponse.from(product))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    fun updateProduct(
        @PathVariable productId: Long,
        @RequestBody updateProductRequest: UpdateProductRequest
    ): ResponseEntity<ProductResponse> {
        val updatedProduct = productService.updateProduct(productId, updateProductRequest)
        return ResponseEntity.ok(ProductResponse.from(updatedProduct))
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable productId: Long): ResponseEntity<Void> {
        productService.deleteProductById(productId)
        return ResponseEntity.noContent().build()
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/archive")
    fun archiveProducts(@RequestBody productIds: List<Long>): ResponseEntity<List<ProductResponse>> {
        val archivedProducts = productService.archiveProducts(productIds)
        return ResponseEntity.ok(archivedProducts.map { ProductResponse.from(it) })
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/unarchive")
    fun unarchiveProducts(@RequestBody productIds: List<Long>): ResponseEntity<List<ProductResponse>> {
        val archivedProducts = productService.unarchiveProducts(productIds)
        return ResponseEntity.ok(archivedProducts.map { ProductResponse.from(it) })
    }
}
