package com.xu_store.uniform.model

import jakarta.persistence.*
import java.time.Instant


@Entity
@Table(name = "product_variations")
data class ProductVariation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(name = "variation_name")
    val variationName: String,

    @Column(name = "display_order")
    val displayOrder: Int = 0,

    val price: Long,

    @Column(name = "created_at")
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now()
)
