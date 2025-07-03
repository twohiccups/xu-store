package com.xu_store.uniform.repository

import com.xu_store.uniform.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun findByTeamIsNull(): List<User>

    // In UserRepository
    @Modifying
    @Query("""
        UPDATE User u 
        SET u.storeCredits = u.storeCredits - :amount 
        WHERE u.id = :userId AND u.storeCredits >= :amount
    """)
    fun deductUserCredits(@Param("userId") userId: Long, @Param("amount") amount: Long): Int


    @Modifying
    @Query("UPDATE User u SET u.storeCredits = u.storeCredits + :amount WHERE u.id = :userId")
    fun incrementUserCredits(@Param("userId") userId: Long, @Param("amount") amount: Long): Int
}
