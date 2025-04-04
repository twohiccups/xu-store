package com.xu_store.uniform.repository

import com.xu_store.uniform.model.Product
import com.xu_store.uniform.model.ProductVariation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface ProductRepository: JpaRepository<Product, Long> {

//    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.productVariations WHERE p.archived = false ORDER BY p.createdAt DESC")
//    fun findAllWithVariations(): List<Product>

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.productVariations WHERE p.archived = false ORDER BY p.createdAt DESC")
    fun findAllWithVariations(): List<Product>

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.productVariations WHERE p.archived = true ORDER BY p.createdAt DESC")
    fun findAllArchivedWithVariations(): List<Product>

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.productVariations WHERE p.id = :id and p.archived = false")
    fun findByIdWithVariations(@Param("id") id: Long): Optional<Product>

    @Query("SELECT pv.price FROM ProductVariation pv WHERE pv.id = :id")
    fun findVariationPrice(@Param("id") id: Long): Optional<Long>



    @Query(
        value = """
        SELECT DISTINCT p.* 
        FROM products p
        JOIN product_group_assignments pga ON pga.product_id = p.id
        JOIN team_product_groups tpg ON tpg.product_group_id = pga.product_group_id
        WHERE tpg.team_id = :team_id AND p.archived = false
        
    """,
        nativeQuery = true
    )
    fun findAllByTeamId(@Param("team_id") teamId: Long?): List<Product>

    fun getProductByProductVariations_Id(productVariationId: Long): Product

    @Query("SELECT pv FROM ProductVariation pv WHERE pv.id = :id")
    fun findProductVariationById(@Param("id") id: Long): Optional<ProductVariation>


}




