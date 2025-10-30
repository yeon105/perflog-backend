package com.perflog.domain.perfume.repository

import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.perfume.model.entity.PerfumeTag
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PerfumeTagRepository : JpaRepository<PerfumeTag, Long> {
    fun findByPerfume(perfume: Perfume): List<PerfumeTag>

    @Modifying
    @Query("delete from PerfumeTag pt where pt.perfume.id = :perfumeId")
    fun deleteByPerfumeId(@Param("perfumeId") perfumeId: Long)

    @Query(
        """
    select t.id
    from PerfumeTag pt
    join pt.tag t
    where pt.perfume.id in :perfumeIds
    group by t.id
    order by count(pt.id) desc
"""
    )
    fun findTopTagsByPerfumeIds(perfumeIds: List<Long>, pageable: Pageable): List<Long>

    @Query(
        """
    select pt.perfume
    from PerfumeTag pt
    where pt.tag.id IN :tagIds
    group by pt.perfume
    having count(pt.tag.id) >= 2
"""
    )
    fun findPerfumesByMatchingTags(tagIds: List<Long>): List<Perfume>
}