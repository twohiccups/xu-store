package com.xu_store.uniform.service

import com.xu_store.uniform.dto.CreateProductRequest
import com.xu_store.uniform.dto.UpdateProductRequest
import com.xu_store.uniform.model.Product
import com.xu_store.uniform.model.ProductVariation
import com.xu_store.uniform.model.User
import com.xu_store.uniform.repository.ProductRepository
import com.xu_store.uniform.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class ProductService (
    val productRepository: ProductRepository,
    val userRepository: UserRepository,
)
{



    fun listAllProducts() : List<Product>  {
        return productRepository.findAllWithVariations()
    }

    fun listProductsForUser(email: String): List<Product> {
        val user = userRepository.findByEmail(email)
        return productRepository.findAllByTeamId(user?.team?.id)
    }


    @Transactional
    fun createProductWithVariations(request: CreateProductRequest): Product {
        // Create the product instance
        val product = Product(
            name = request.name,
            description = request.description
        )

        request.variations.forEach { variationRequest ->
            val variation = ProductVariation(
                product = product,  // associate with the product
                variationName = variationRequest.variationName,
                price = variationRequest.price
            )
            product.variations.add(variation)
        }

        // Persist the product (cascading will handle the variations)
        return productRepository.save(product)
    }


    fun deleteProductById(productId: Long) {
        productRepository.deleteById(productId)
    }


    @Transactional
    fun updateProduct(productId: Long, request: UpdateProductRequest): Product {
        // Fetch the product (with variations eagerly loaded)
        val product = productRepository.findByIdWithVariations(productId)
            .orElseThrow { RuntimeException("Product not found") }

        // Create a lookup for update requests that refer to an existing variation
        val requestVariationsById = request.productVariations
            .filter { it.id != null }
            .associateBy { it.id!! }

        // Process existing variations: update those still in the request.
        val updatedExistingVariations = product.variations
            .filter { it.id in requestVariationsById.keys }
            .map { existing ->
                val updateRequest = requestVariationsById[existing.id]!!
                // Use copy() on the variation (data classes automatically provide copy)
                existing.copy(
                    variationName = updateRequest.variationName,
                    price = updateRequest.price
                )
            }

        // Process new variations (those with no id in the request)
        val newVariations = request.productVariations
            .filter { it.id == null }
            .map { newVarRequest ->
                ProductVariation(
                    product = product, // Temporary: will be set properly on the new Product copy.
                    variationName = newVarRequest.variationName,
                    price = newVarRequest.price
                )
            }

        // Combine updated existing variations with new ones
        val updatedVariations = (updatedExistingVariations + newVariations).toMutableList()

        // Create a new Product instance using copy() with updated fields and variations.
        // Note: The copy() method will keep the same id, createdAt, etc.
        val updatedProduct = product.copy(
            name = request.name,
            description = request.description,
            variations = updatedVariations
        )

        // Save the updated product; since it has the same id, JPA will merge it.
        return productRepository.save(updatedProduct)
    }

    @Transactional
    fun archiveProducts(productIds: List<Long>): List<Product> {
        // Fetch all products with the given IDs.
        val products = productRepository.findAllById(productIds)
        products.forEach { it.archived = true }
        return productRepository.saveAll(products)
    }

}



