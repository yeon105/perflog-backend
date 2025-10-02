package com.perflog.domain.perfume.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "tags")
class Tag(

    @Column(nullable = false)
    var name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @OneToMany(mappedBy = "tag")
    val perfumeTags: MutableList<PerfumeTag> = mutableListOf()
}