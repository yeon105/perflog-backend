package com.perflog.domain.perfume.model.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "perfume_tags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["perfume_id", "tag_id"])]
)
class PerfumeTag(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfume_id", nullable = false)
    var perfume: Perfume,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    var tag: Tag
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
