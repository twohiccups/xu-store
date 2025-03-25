package com.xu_store.uniform.controller

import com.xu_store.uniform.dto.*
import com.xu_store.uniform.model.ProductGroupResponse
import com.xu_store.uniform.model.ProductGroupsResponse
import com.xu_store.uniform.service.ProductGroupService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/product-groups")
class ProductGroupController(
    private val productGroupService: ProductGroupService
) {


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createProductGroup(@RequestBody request: CreateProductGroupRequest): ResponseEntity<ProductGroupResponse> {
        val productGroup = productGroupService.createProductGroup(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductGroupResponse.from(productGroup))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{groupId}")
    fun updateProductGroup(
        @PathVariable groupId: Long,
        @RequestBody request: UpdateProductGroupRequest
    ): ResponseEntity<ProductGroupResponse> {
        val productGroup = productGroupService.updateProductGroup(groupId, request)
        return ResponseEntity.ok(ProductGroupResponse.from(productGroup))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{groupId}")
    fun deleteProductGroup(@PathVariable groupId: Long): ResponseEntity<Void> {
        productGroupService.deleteProductGroup(groupId)
        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{groupId}/products")
    fun addProductsToGroup(
        @PathVariable groupId: Long,
        @RequestBody request: AddProductsToProductGroupRequest
    ): ResponseEntity<ProductGroupResponse> {
        val productGroup = productGroupService.addProductsToGroup(groupId, request)
        return ResponseEntity.ok(ProductGroupResponse.from(productGroup))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{groupId}/products")
    fun removeProductsFromGroup(
        @PathVariable groupId: Long,
        @RequestBody request: RemoveProductsFromProductGroupRequest
    ): ResponseEntity<ProductGroupResponse> {
        val productGroup = productGroupService.removeProductsFromGroup(groupId, request)
        return ResponseEntity.ok(ProductGroupResponse.from(productGroup))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{groupId}/teams")
    fun addTeamsToGroup(
        @PathVariable groupId: Long,
        @RequestBody request: AddTeamsToProductGroupRequest
    ): ResponseEntity<ProductGroupResponse> {
        val productGroup = productGroupService.addTeamsToGroup(groupId, request)
        return ResponseEntity.ok(ProductGroupResponse.from(productGroup))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{groupId}/teams")
    fun removeTeamsFromGroup(
        @PathVariable groupId: Long,
        @RequestBody request: RemoveTeamsFromProductGroupRequest
    ): ResponseEntity<Unit> {
        productGroupService.removeTeamsFromGroup(groupId, request)
        return ResponseEntity.ok(Unit)
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
        fun getAllGroups() : ProductGroupsResponse {
            val groups = productGroupService.findAllProductGroups()
            return ProductGroupsResponse.from(groups)
        }
}

