package com.perflog.domain.preference.service

import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.domain.member.repository.MemberRepository
import com.perflog.domain.perfume.repository.PerfumeRepository
import com.perflog.domain.preference.dto.PreferenceDto
import com.perflog.domain.preference.model.PerfumePreference
import com.perflog.domain.preference.model.PreferenceStatus
import com.perflog.domain.preference.repository.PerfumePreferenceRepository
import jakarta.transaction.Transactional
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class PerfumePreferenceServiceImpl(
    private val preferenceRepository: PerfumePreferenceRepository,
    private val memberRepository: MemberRepository,
    private val perfumeRepository: PerfumeRepository
) : PerfumePreferenceService {

    override fun recordPerfumePreference(
        id: Long,
        request: PreferenceDto.CreateRequest,
        authentication: Authentication
    ) {
        val member = memberRepository.findByEmail(authentication.name)
            ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND)

        val perfume = perfumeRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PERFUME_NOT_FOUND) }

        val perfumePreference = PerfumePreference(
            member = member,
            perfume = perfume,
            status = PreferenceStatus.valueOf(request.status.uppercase()),
            usedAt = request.usedAt?.let { LocalDate.parse(it) } ?: LocalDate.now()
        )

        preferenceRepository.save(perfumePreference)
    }
}