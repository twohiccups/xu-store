package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateProductRequest
import com.xu_store.uniform.dto.CreateProductVariationRequest
import com.xu_store.uniform.dto.UpdateProductRequest
import com.xu_store.uniform.dto.UpdateProductVariationRequest
import com.xu_store.uniform.model.Team
import com.xu_store.uniform.model.Product
import com.xu_store.uniform.model.ProductVariation
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.ProductRepository
import com.xu_store.uniform.repository.UserRepository
import org.junit.jupiter.api.Test
import java.time.Instant



import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.time.temporal.ChronoUnit
import java.util.*

class ProductServiceTest {

    private val productRepository: ProductRepository = mock(ProductRepository::class.java)

    // We don't need to mock userRepository for delete tests
    private val userRepository = mock(UserRepository::class.java)

    private val testProductId: Long = 1L

    private val productService = ProductService(productRepository, userRepository)

    @Test
    fun getAllProducts() {
        productService.getAllProducts()
        verify(productRepository, times(1)).findAllWithVariations()

    }


    @Test
    fun `deleteProductById calls repository deleteById with correct id`() {

        // Call the service method
        productService.deleteProductById(testProductId)

        // Verify that productRepository.deleteById was called exactly once with productId
        verify(productRepository, times(1)).deleteById(testProductId)
    }

    @Test
    fun `listAllProducts calls repository findAllWithVariations once`() {
        productService.getAllProducts()
        verify(productRepository, times(1)).findAllWithVariations()
    }

    @Test
    fun `listProductsForUser returns expected products for valid user`() {
        val email = "test@example.com"
        val teamId = 100L

        val team = Team(
            id = teamId,
            name = "Team A",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val user = User(
            id = 1L,
            email = email,
            passwordHash = "hashedPassword",
            role = "USER",
            storeCredits = 0,
            team = team,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        whenever(userRepository.findByEmail(email)).thenReturn(user)

        // Create sample products that should be returned for the team
        val product1 = Product(
            id = 1L,
            name = "Product 1",
            description = "First product",
            productVariations = mutableListOf(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val product2 = Product(
            id = 2L,
            name = "Product 2",
            description = "Second product",
            productVariations = mutableListOf(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        whenever(productRepository.findAllByTeamId(teamId)).thenReturn(listOf(product1, product2))

        val products = productService.getProductsForUserByEmail(email)

        assertNotNull(products)
        assertEquals(2, products.size)
        assertEquals("Product 1", products[0].name)
        assertEquals("Product 2", products[1].name)
    }

    @Test
    fun `createProductWithVariations creates product with provided variations and calls save`() {
        // Arrange: create a sample request with two variations.
        // (Assuming your CreateProductRequest has a nested VariationRequest class.)
        val variationRequest1 = CreateProductVariationRequest(
            variationName = "Variation A",
            displayOrder = 0,
            price = 1000
        )
        val variationRequest2 = CreateProductVariationRequest(
            variationName = "Variation B",
            displayOrder = 0,
            price = 1500
        )
        val createRequest = CreateProductRequest(
            name = "Test Product",
            description = "Test product description",
            productVariations = listOf(variationRequest1, variationRequest2)
        )

        // Simulate that the repository returns the saved product with an assigned ID.
        // For simplicity, we assume that the saved product is the one passed in (with id set) by our repository.
        val savedProduct = Product(
            id = 1L,
            name = createRequest.name,
            description = createRequest.description,
            productVariations = mutableListOf(
                // Here we simulate that variations are saved as well.
                ProductVariation(id = 10L, product = Product(id = 1L, name = createRequest.name, description = createRequest.description, productVariations = mutableListOf()), variationName = "Variation A", displayOrder = 0, price = 1000),
                ProductVariation(id = 11L, product = Product(id = 1L, name = createRequest.name, description = createRequest.description, productVariations = mutableListOf()), variationName = "Variation B", displayOrder = 0, price = 1500)
            ),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        whenever(productRepository.save(any(Product::class.java))).thenReturn(savedProduct)

        // Act
        val result = productService.createProductWithVariations(createRequest)

        // Assert
        assertNotNull(result)
        assertEquals("Test Product", result.name)
        assertEquals(2, result.productVariations.size)
        verify(productRepository, times(1)).save(any(Product::class.java))
    }

    @Test
    fun `updateProduct updates existing product and adds new variations`() {
        // Arrange: Create an existing product with one variation.


        val existingProduct = Product(
            id = 1L,
            name = "Old Product Name",
            description = "Old Description",
            productVariations = mutableListOf(),
            createdAt = Instant.now().minus(1, ChronoUnit.DAYS),
            updatedAt = Instant.now().minus(1, ChronoUnit.DAYS)
        )

        // Now create an existing variation that references the existing product.
        val existingVariation = ProductVariation(
            id = 100L,
            product = existingProduct,
            variationName = "Old Variation",
            displayOrder = 0,
            price = 1000
        )
        existingProduct.productVariations.add(existingVariation)

        whenever(productRepository.findByIdWithVariations(1L)).thenReturn(Optional.of(existingProduct))

        // Create an update request that updates the existing variation and adds a new one.
        val updateRequest = UpdateProductRequest(
            name = "New Product Name",
            description = "New Description",
            productVariations = listOf(
                // Update existing variation.
                UpdateProductVariationRequest(
                    id = 100L,
                    variationName = "Updated Variation",
                    displayOrder = 0,
                    price = 1200
                ),
                // Add a new variation.
                UpdateProductVariationRequest(
                    id = null,
                    variationName = "New Variation",
                    displayOrder = 0,
                    price = 1500
                )
            )
        )

        // Simulate save() by returning the product passed to it.
        whenever(productRepository.save(any(Product::class.java))).thenAnswer { it.arguments[0] as Product }

        // Act
        val updatedProduct = productService.updateProduct(1L, updateRequest)

        // Assert
        assertEquals("New Product Name", updatedProduct.name)
        assertEquals("New Description", updatedProduct.description)
        // Expecting two variations: one updated and one new.
        assertEquals(2, updatedProduct.productVariations.size)

        val updatedExistingVariation = updatedProduct.productVariations.find { it.id == 100L }
        assertNotNull(updatedExistingVariation)
        assertEquals("Updated Variation", updatedExistingVariation!!.variationName)
        assertEquals(1200, updatedExistingVariation.price)

        val newVariation = updatedProduct.productVariations.find { it.variationName == "New Variation" }
        assertNotNull(newVariation)
        assertEquals(1500, newVariation!!.price)

        verify(productRepository, times(1)).findByIdWithVariations(1L)
        verify(productRepository, times(1)).save(any(Product::class.java))
    }


}