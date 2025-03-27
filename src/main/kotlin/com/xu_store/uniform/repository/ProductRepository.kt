package com.xu_store.uniform.repository

import com.xu_store.uniform.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface ProductRepository: JpaRepository<Product, Long> {

//    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variations WHERE p.archived = false ORDER BY p.createdAt DESC")
//    fun findAllWithVariations(): List<Product>

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variations LEFT JOIN FETCH p.groups WHERE p.archived = false ORDER BY p.createdAt DESC")
    fun findAllWithVariations(): List<Product>

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variations WHERE p.id = :id and p.archived = false")
    fun findByIdWithVariations(@Param("id") id: Long): Optional<Product>


    @Query(
        value = """
        SELECT DISTINCT p.* 
        FROM products p
        JOIN product_group_assignments pga ON pga.product_id = p.id
        JOIN team_product_groups tpg ON tpg.product_group_id = pga.product_group_id
        WHERE tpg.team_id = :team_id AND p.archived = 0
        
    """,
        nativeQuery = true
    )
    fun findAllByTeamId(@Param("team_id") teamId: Long?): List<Product>
}




