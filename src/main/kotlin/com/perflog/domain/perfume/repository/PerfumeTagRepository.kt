package com.perflog.domain.perfume.repository

import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.perfume.model.entity.PerfumeTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PerfumeTagRepository : JpaRepository<PerfumeTag, Long> {
    fun findByPerfume(perfume: Perfume): List<PerfumeTag>

    @Modifying
    @Query("delete from PerfumeTag pt where pt.perfume.id = :perfumeId")
    fun deleteByPerfumeId(@Param("perfumeId") perfumeId: Long)
}