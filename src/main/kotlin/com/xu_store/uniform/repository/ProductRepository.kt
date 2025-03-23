package com.xu_store.uniform.repository

import com.xu_store.uniform.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface ProductRepository: JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variations")
    fun findAllWithVariations(): List<Product>

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variations WHERE p.id = :id")
    fun findByIdWithVariations(@Param("id") id: Long): Optional<Product>
}

