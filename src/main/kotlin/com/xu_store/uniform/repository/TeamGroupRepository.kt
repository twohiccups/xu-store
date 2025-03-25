package com.xu_store.uniform.repository

import com.xu_store.uniform.model.TeamProductGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TeamProductGroupRepository : JpaRepository<TeamProductGroup, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM TeamProductGroup tpg WHERE tpg.team.id = :teamId AND tpg.productGroup.id = :productGroupId")
    fun deleteByTeamIdAndGroupId(@Param("teamId") teamId: Long, @Param("productGroupId") productGroupId: Long)
}
