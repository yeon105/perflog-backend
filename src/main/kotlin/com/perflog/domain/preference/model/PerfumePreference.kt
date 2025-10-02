package com.perflog.domain.preference.model

import com.perflog.domain.member.model.Member
import com.perflog.domain.perfume.model.entity.Perfume
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "perfume_preferences")
class PerfumePreference(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfume_id", nullable = false)
    var perfume: Perfume,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PreferenceStatus,

    @Column(name = "used_at", nullable = true)
    var usedAt: LocalDate? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}