package com.xu_store.uniform.model

import com.xu_store.uniform.model.ProductGroup
import com.xu_store.uniform.model.Team
import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "team_product_groups")
data class TeamProductGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    val team: Team,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_group_id", nullable = false)
    val productGroup: ProductGroup,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
