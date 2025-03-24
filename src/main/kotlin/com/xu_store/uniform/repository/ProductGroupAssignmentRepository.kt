package com.xu_store.uniform.repository

import com.xu_store.uniform.model.ProductGroupAssignment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductGroupAssignmentRepository : JpaRepository<ProductGroupAssignment, Long>
