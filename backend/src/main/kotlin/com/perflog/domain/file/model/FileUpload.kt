package com.perflog.domain.file.model

import com.perflog.domain.member.model.Member
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "file_uploads")
class FileUpload(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @Column(name = "filename")
    val filename: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(name = "uploaded_at")
    val uploadedAt: LocalDateTime = LocalDateTime.now()
}