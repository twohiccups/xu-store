package com.xu_store.uniform.service

import com.xu_store.uniform.dto.*
import com.xu_store.uniform.model.ProductGroup
import com.xu_store.uniform.model.ProductGroupAssignment
import com.xu_store.uniform.model.TeamProductGroup
import com.xu_store.uniform.repository.ProductGroupAssignmentRepository
import com.xu_store.uniform.repository.ProductGroupRepository
import com.xu_store.uniform.repository.TeamProductGroupRepository
import com.xu_store.uniform.repository.ProductRepository
import com.xu_store.uniform.repository.TeamRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class ProductGroupService(
    private val productGroupRepository: ProductGroupRepository,
    private val productGroupAssignmentRepository: ProductGroupAssignmentRepository,
    private val teamProductGroupRepository: TeamProductGroupRepository,
    private val productRepository: ProductRepository,
    private val teamRepository: TeamRepository
) {

    fun createProductGroup(request: CreateProductGroupRequest): ProductGroup {
        val productGroup = ProductGroup(
            name = request.name,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return productGroupRepository.save(productGroup)
    }

    fun getProductGroup(groupId: Long): Optional<ProductGroup> {
        return productGroupRepository.findById(groupId)
    }

    fun updateProductGroup(groupId: Long, request: UpdateProductGroupRequest): ProductGroup {
        val existingGroup = productGroupRepository.findById(groupId)
            .orElseThrow { RuntimeException("ProductGroup not found with id: $groupId") }
        val updatedGroup = existingGroup.copy(
            name = request.name ?: existingGroup.name,
            updatedAt = LocalDateTime.now()
        )
        return productGroupRepository.save(updatedGroup)
    }

    fun deleteProductGroup(id: Long) {
        productGroupRepository.deleteById(id)
    }

    @Transactional
    fun addProductsToGroup(productGroupId: Long, request: AddProductsToProductGroupRequest): ProductGroup {
        val productGroup = productGroupRepository.findById(productGroupId)
            .orElseThrow { RuntimeException("ProductGroup not found with id: $productGroupId") }
        request.productIds.forEach { productId ->
            val product = productRepository.findById(productId)
                .orElseThrow { RuntimeException("Product not found with id: $productId") }
            // Avoid duplicates
            if (productGroup.productGroupAssignments.none { it.product.id == productId }) {
                val assignment = ProductGroupAssignment(
                    product = product,
                    productGroup = productGroup
                )
                productGroup.productGroupAssignments.add(assignment)
            }
        }
        return productGroupRepository.save(productGroup)
    }

    @Transactional
    fun removeProductsFromGroup(productGroupId: Long, request: RemoveProductsFromProductGroupRequest): ProductGroup {

        val productGroup = productGroupRepository.findById(productGroupId)
            .orElseThrow { RuntimeException("ProductGroup not found with id: $productGroupId") }
        productGroup.productGroupAssignments.removeIf { assignment ->
            request.productIds.contains(assignment.product.id)
        }
        return productGroupRepository.save(productGroup)
    }

    @Transactional
    fun addTeamsToGroup(productGroupId: Long, request: AddTeamsToProductGroupRequest): ProductGroup {
        val productGroup = productGroupRepository.findById(productGroupId)
            .orElseThrow { RuntimeException("ProductGroup not found with id: $productGroupId") }
        request.teamIds.forEach { teamId ->
            val team = teamRepository.findById(teamId)
                .orElseThrow { RuntimeException("Team not found with id: $teamId") }
            if (productGroup.teamProductGroups.none { it.team.id == teamId }) {
                val teamAssignment = TeamProductGroup(
                    team = team,
                    productGroup = productGroup
                )
                productGroup.teamProductGroups.add(teamAssignment)
            }
        }
        return productGroupRepository.save(productGroup)
    }

    @Transactional
    fun removeTeamsFromGroup(productGroupId: Long, request: RemoveTeamsFromProductGroupRequest) {
        request.teamIds.forEach { teamId -> teamProductGroupRepository.deleteByTeamIdAndGroupId(teamId, productGroupId)}
    }

    fun findAllProductGroups() : List<ProductGroup> {
        return productGroupRepository.findAll()
    }
}
