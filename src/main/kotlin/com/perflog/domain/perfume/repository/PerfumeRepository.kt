package com.perflog.domain.perfume.repository

import com.perflog.domain.perfume.model.entity.Perfume
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PerfumeRepository : JpaRepository<Perfume, Long> {
    fun existsByNameAndBrand(name: String, brand: String): Boolean

    @EntityGraph(attributePaths = ["perfumeTags", "perfumeTags.tag"])
    fun findWithTagsById(id: Long): Optional<Perfume>

    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Perfume>

    fun findByBrandContainingIgnoreCase(brand: String, pageable: Pageable): Page<Perfume>

    fun findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
        name: String,
        brand: String,
        pageable: Pageable
    ): Page<Perfume>
}