package com.perflog.domain.perfume.repository

import com.perflog.domain.perfume.model.entity.Perfume
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
        select distinct p from Perfume p
        left join fetch p.perfumeTags pt
        left join fetch pt.tag
        where p.id > :lastId
        order by p.id asc
    """
    )
    fun findBatchAfterId(
        @Param("lastId") lastId: Long,
        pageable: Pageable
    ): List<Perfume>
}