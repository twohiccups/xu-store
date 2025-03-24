package com.xu_store.uniform.repository

import com.xu_store.uniform.model.Team
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface TeamRepository : JpaRepository<Team, Long>