package com.xu_store.uniform.controller


import com.xu_store.uniform.dto.*
import com.xu_store.uniform.security.CustomUserDetails
import com.xu_store.uniform.service.ProductService
import com.xu_store.uniform.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")

class ProductController(
    private val productService: ProductService,
    private val userService: UserService
) {

    // This endpoint is available for every authenticated user (any role)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current")
    fun getProductsForCurrentUser(@AuthenticationPrincipal currentUser: CustomUserDetails): ResponseEntity<List<ProductResponse>> {
        val user = userService.getUserByEmail(currentUser.username) ?: throw UsernameNotFoundException("User doesn't exist")
        val products = productService.getProductsForUserByEmail(user.email)
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
    @PostMapping("/{productId}/images")
    fun saveProductImage(
        @PathVariable productId: Long,
        @RequestBody saveImageRequest: SaveImageRequest
    ): ResponseEntity<ProductImageResponse> {
        val product = productService.getProductById(productId).get()
        val productImage = productService.saveImage(product, saveImageRequest.imageUrl )
        return ResponseEntity.ok(ProductImageResponse.from(productImage))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}/images/{imageId}")
    fun deleteProductImage(
        @PathVariable productId: Long,
        @PathVariable imageId: Long,
    ) {
        val product = productService.getProductById(productId).get()
        productService.deleteImageById(product, imageId)
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getAllProducts()
        return ResponseEntity.ok(products.map { ProductResponse.from(it) })
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/archived")
    fun getAllArchivedProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getAllArchivedProducts()
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
