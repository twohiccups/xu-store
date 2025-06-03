package com.xu_store.uniform.model

import jakarta.persistence.*
import java.time.Instant


@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val email: String,

    @Column(name = "password_hash")
    val passwordHash: String,

    val role: String = "USER",

    @Column(name = "store_credits")
    val storeCredits: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val team: Team? = null,

    @Column(name = "created_at")
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now()


)
