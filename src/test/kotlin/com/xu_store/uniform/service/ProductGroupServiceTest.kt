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
import java.time.Instant

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



}
