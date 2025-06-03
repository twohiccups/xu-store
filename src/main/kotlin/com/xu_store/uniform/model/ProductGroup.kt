package com.xu_store.uniform.model

import java.time.Instant

import jakarta.persistence.*

@Entity
@Table(name = "product_groups")
data class ProductGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    @Column(name = "created_at")
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),

    // List of products assigned to this group
    @OneToMany(mappedBy = "productGroup", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val productGroupAssignments: MutableList<ProductGroupAssignment> = mutableListOf(),

    // List of teams associated with this group
    @OneToMany(mappedBy = "productGroup", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val teamProductGroups: MutableList<TeamProductGroup> = mutableListOf()
)
