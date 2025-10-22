package com.perflog.domain.member.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.perflog.common.model.BaseTimeEntity
import com.perflog.domain.review.model.Review
import jakarta.persistence.*

@Entity
@Table(name = "members")
class Member(

    @Column(name = "email", nullable = false)
    var email: String,

    @Column(name = "password", nullable = false)
    var password: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    var role: MemberRole
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @JsonManagedReference("member-reviews")
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    val reviews: MutableList<Review> = mutableListOf()
}