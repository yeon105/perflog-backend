package com.perflog.domain.review.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.perflog.common.model.BaseTimeEntity
import com.perflog.domain.member.model.Member
import com.perflog.domain.perfume.model.entity.Perfume
import jakarta.persistence.*

@Entity
@Table(name = "reviews")
class Review(

    @JsonBackReference("member-reviews")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfume_id", nullable = false)
    val perfume: Perfume,

    @Column(nullable = false)
    var rating: Int,

    @Column(columnDefinition = "TEXT")
    var content: String
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}