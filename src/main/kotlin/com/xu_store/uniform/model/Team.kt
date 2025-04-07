package com.xu_store.uniform.model

import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "teams")
data class Team(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    @Column(name = "shipping_fee")
    var shippingFee: Long = 0,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    // Optional: One-to-many if you want to access Users from a Team.
    @OneToMany(mappedBy = "team", cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.LAZY)
    @OrderBy ("email ASC")
    val users: MutableList<User> = mutableListOf()
)

