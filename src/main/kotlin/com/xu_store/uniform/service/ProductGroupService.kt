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
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

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
        val existingGroup = productGroupRepository.findByName(request.name)
        if (existingGroup != null) {
            throw IllegalArgumentException("Group with name ${request.name} already exists")
        }
        val productGroup = ProductGroup(
            name = request.name,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        return productGroupRepository.save(productGroup)
    }

    fun getProductGroup(groupId: Long): Optional<ProductGroup> {
        return productGroupRepository.findById(groupId)
    }

    fun updateProductGroup(groupId: Long, request: UpdateProductGroupRequest): ProductGroup {
        val existingGroup = productGroupRepository.findById(groupId)
            .orElseThrow { IllegalArgumentException("ProductGroup not found with id: $groupId") }
        val updatedGroup = existingGroup.copy(
            name = request.name,
            updatedAt = Instant.now()
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

        // Determine current max display order (or -1 if none)
        var nextDisplayOrder = productGroup.productGroupAssignments
            .maxOfOrNull { it.displayOrder } ?: -1

        request.productIds.forEach { productId ->
            val product = productRepository.findById(productId)
                .orElseThrow { RuntimeException("Product not found with id: $productId") }

            // Avoid duplicates
            if (productGroup.productGroupAssignments.none { it.product.id == productId }) {
                val assignment = ProductGroupAssignment(
                    product = product,
                    productGroup = productGroup,
                    displayOrder = ++nextDisplayOrder
                )
                productGroup.productGroupAssignments.add(assignment)
            }
        }

        return productGroupRepository.save(productGroup)
    }


    private fun ProductGroupRepository.findByIdOrThrow(id: Long): ProductGroup =
        findById(id).orElseThrow { EntityNotFoundException("ProductGroup $id not found") }


    @Transactional
    fun removeProductsFromGroup(groupId: Long, request: RemoveProductsFromProductGroupRequest): ProductGroup {
        val group = productGroupRepository.findByIdOrThrow(groupId)

        // Remove the assignments
        val toRemove = group.productGroupAssignments
            .filter { assignment -> request.productIds.contains(assignment.product.id) }

        group.productGroupAssignments.removeAll(toRemove)

        // Reassign displayOrder only if changed
        group.productGroupAssignments
            .sortedBy { it.displayOrder }
            .forEachIndexed { index, assignment ->
                if (assignment.displayOrder != index) {
                    assignment.displayOrder = index
                }
            }

        return group
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


    @Transactional
    fun reorderProductsInGroup(groupId: Long, request: ReorderProductGroupRequest): ProductGroup {
        val group = productGroupRepository.findByIdOrThrow(groupId)

        val assignmentMap = group.productGroupAssignments.associateBy { it.product.id }

        request.orderedProductIds.forEachIndexed { index, productId ->
            val assignment = assignmentMap[productId]
                ?: throw IllegalArgumentException("Product ID $productId not found in group $groupId")

            if (assignment.displayOrder != index) {
                assignment.displayOrder = index
            }
        }

        return group
    }

}
