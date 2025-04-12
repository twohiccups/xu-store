package com.xu_store.uniform.service

import com.xu_store.uniform.dto.*
import com.xu_store.uniform.model.Product
import com.xu_store.uniform.model.ProductGroup
import com.xu_store.uniform.model.ProductGroupAssignment
import com.xu_store.uniform.model.Team
import com.xu_store.uniform.repository.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.*

class ProductGroupServiceTest {

 private val productGroupRepository: ProductGroupRepository = mock(ProductGroupRepository::class.java)
 private val productGroupAssignmentRepository: ProductGroupAssignmentRepository = mock(ProductGroupAssignmentRepository::class.java)
 private val teamProductGroupRepository: TeamProductGroupRepository = mock(TeamProductGroupRepository::class.java)
 private val productRepository: ProductRepository = mock(ProductRepository::class.java)
 private val teamRepository: TeamRepository = mock(TeamRepository::class.java)

 private val service = ProductGroupService(
  productGroupRepository,
  productGroupAssignmentRepository,
  teamProductGroupRepository,
  productRepository,
  teamRepository
 )


 @Test
 fun `createProductGroup creates and returns new product group`() {
  // Arrange
  val request = CreateProductGroupRequest(name = "Group A")
  val teamGroup = ProductGroup(
   id = null,
   name = request.name,
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now()
  )
  // Simulate repository save returning a group with an ID.
  val savedGroup = teamGroup.copy(id = 1L)
  whenever(productGroupRepository.save(any(ProductGroup::class.java))).thenReturn(savedGroup)

  // Act
  val result = service.createProductGroup(request)

  // Assert
  assertNotNull(result)
  assertEquals(1L, result.id)
  assertEquals("Group A", result.name)
  verify(productGroupRepository, times(1)).save(any(ProductGroup::class.java))
 }

 @Test
 fun `updateProductGroup updates existing product group`() {
  // Arrange
  val groupId = 1L
  val existingGroup = ProductGroup(
   id = groupId,
   name = "Old Group",
   createdAt = LocalDateTime.now().minusDays(1),
   updatedAt = LocalDateTime.now().minusDays(1)
  )
  val updateRequest = UpdateProductGroupRequest(name = "Updated Group")
  whenever(productGroupRepository.findById(groupId)).thenReturn(Optional.of(existingGroup))
  whenever(productGroupRepository.save(any(ProductGroup::class.java))).thenAnswer { it.arguments[0] as ProductGroup }

  // Act
  val updatedGroup = service.updateProductGroup(groupId, updateRequest)

  // Assert
  assertNotNull(updatedGroup)
  assertEquals("Updated Group", updatedGroup.name)
  assertTrue(updatedGroup.updatedAt.isAfter(existingGroup.updatedAt))
  verify(productGroupRepository, times(1)).findById(groupId)
  verify(productGroupRepository, times(1)).save(any(ProductGroup::class.java))
 }

 @Test
 fun `deleteProductGroup calls repository deleteById`() {
  // Arrange
  val groupId = 1L

  // Act
  service.deleteProductGroup(groupId)

  // Assert
  verify(productGroupRepository, times(1)).deleteById(groupId)
 }

 @Test
 fun `addProductsToGroup adds products to group without duplicates`() {
  // Arrange
  val groupId = 1L
  val group = ProductGroup(
   id = groupId,
   name = "Group A",
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now(),
   productGroupAssignments = mutableListOf()
  )
  whenever(productGroupRepository.findById(groupId)).thenReturn(Optional.of(group))
  // Create two products to add.
  val product1 = Product(
   id = 101L,
   name = "Product 101",
   description = "Desc",
   productVariations = mutableListOf(),
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now()
  )
  val product2 = Product(
   id = 102L,
   name = "Product 102",
   description = "Desc",
   productVariations = mutableListOf(),
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now()
  )
  whenever(productRepository.findById(101L)).thenReturn(Optional.of(product1))
  whenever(productRepository.findById(102L)).thenReturn(Optional.of(product2))
  whenever(productGroupRepository.save(any(ProductGroup::class.java))).thenAnswer { it.arguments[0] as ProductGroup }

  val request = AddProductsToProductGroupRequest(productIds = listOf(101L, 102L))
  val updatedGroup = service.addProductsToGroup(groupId, request)
  // Assert: group should now have 2 assignments.
  assertEquals(2, updatedGroup.productGroupAssignments.size)

  // Duplicate addition should not increase the count.
  val duplicateRequest = AddProductsToProductGroupRequest(productIds = listOf(101L))
  val updatedGroup2 = service.addProductsToGroup(groupId, duplicateRequest)
  assertEquals(2, updatedGroup2.productGroupAssignments.size)

  verify(productGroupRepository, atLeastOnce()).findById(groupId)
  verify(productRepository, times(2)).findById(101L)
  verify(productRepository, times(1)).findById(102L)
  verify(productGroupRepository, atLeastOnce()).save(any(ProductGroup::class.java))
 }

 @Test
 fun `removeProductsFromGroup removes specified products`() {
  // Arrange: Create a product group with two assignments.
  val product1 = Product(
   id = 101L,
   name = "Product 101",
   description = "Desc",
   productVariations = mutableListOf(),
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now()
  )
  val product2 = Product(
   id = 102L,
   name = "Product 102",
   description = "Desc",
   productVariations = mutableListOf(),
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now()
  )
  // Create a group first.
  val group = ProductGroup(
   id = 1L,
   name = "Group A",
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now(),
   productGroupAssignments = mutableListOf()
  )
  // Now create assignments with group set.
  val assignment1 = ProductGroupAssignment(
   id = 1L,
   product = product1,
   productGroup = group,
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now()
  )
  val assignment2 = ProductGroupAssignment(
   id = 2L,
   product = product2,
   productGroup = group,
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now()
  )
  group.productGroupAssignments.addAll(listOf(assignment1, assignment2))

  whenever(productGroupRepository.findById(1L)).thenReturn(Optional.of(group))
  whenever(productGroupRepository.save(any(ProductGroup::class.java))).thenAnswer { it.arguments[0] as ProductGroup }

  val request = RemoveProductsFromProductGroupRequest(productIds = listOf(101L))
  val updatedGroup = service.removeProductsFromGroup(1L, request)

  // Assert: Only product2's assignment should remain.
  assertEquals(1, updatedGroup.productGroupAssignments.size)
  assertEquals(102L, updatedGroup.productGroupAssignments.first().product.id)
 }

 @Test
 fun `addTeamsToGroup adds teams to product group without duplicates`() {
  // Arrange: Create a product group with no team assignments.
  val group = ProductGroup(
   id = 1L,
   name = "Group A",
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now(),
   teamProductGroups = mutableListOf()
  )
  whenever(productGroupRepository.findById(1L)).thenReturn(Optional.of(group))
  // Create two teams.
  val team1 = Team(
   id = 201L,
   name = "Team 201",
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now(),
   shippingFee = 0
  )
  val team2 = Team(
   id = 202L,
   name = "Team 202",
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now(),
   shippingFee = 0,
  )
  whenever(teamRepository.findById(201L)).thenReturn(Optional.of(team1))
  whenever(teamRepository.findById(202L)).thenReturn(Optional.of(team2))
  whenever(productGroupRepository.save(any(ProductGroup::class.java))).thenAnswer { it.arguments[0] as ProductGroup }

  val request = AddTeamsToProductGroupRequest(teamIds = listOf(201L, 202L))
  val updatedGroup = service.addTeamsToGroup(1L, request)
  assertEquals(2, updatedGroup.teamProductGroups.size)

  // Duplicate addition should not add extra teams.
  val duplicateRequest = AddTeamsToProductGroupRequest(teamIds = listOf(201L))
  val updatedGroupDup = service.addTeamsToGroup(1L, duplicateRequest)
  assertEquals(2, updatedGroupDup.teamProductGroups.size)

  verify(teamRepository, times(2)).findById(201L)
  verify(teamRepository, times(1)).findById(202L)
  verify(productGroupRepository, atLeastOnce()).save(any(ProductGroup::class.java))
 }


}
