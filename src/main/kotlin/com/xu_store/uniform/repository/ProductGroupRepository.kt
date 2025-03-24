package com.xu_store.uniform.repository

import com.xu_store.uniform.model.ProductGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductGroupRepository : JpaRepository<ProductGroup, Long>

