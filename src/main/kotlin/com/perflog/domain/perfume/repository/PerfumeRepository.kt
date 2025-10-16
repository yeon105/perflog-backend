package com.perflog.domain.perfume.repository

import com.perflog.domain.perfume.model.entity.Perfume
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface PerfumeRepository : JpaRepository<Perfume, Long> {
    fun existsByNameAndBrand(name: String, brand: String): Boolean

    @EntityGraph(attributePaths = ["perfumeTags", "perfumeTags.tag"])
    fun findWithTagsById(id: Long): Optional<Perfume>

    @Query(
        """
    SELECT p
    FROM Perfume p
    WHERE (:target = 'NAME' AND LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')))
       OR (:target = 'BRAND' AND LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))
"""
    )
    fun searchByTarget(
        @Param("target") target: String,
        @Param("query") query: String,
        pageable: Pageable
    ): Page<Perfume>
}