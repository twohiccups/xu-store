package com.xu_store.uniform.model

import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "product_groups")
data class ProductGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
