package com.perflog.domain.preference.repository

import com.perflog.domain.preference.model.PerfumePreference
import org.springframework.data.jpa.repository.JpaRepository

interface PerfumePreferenceRepository : JpaRepository<PerfumePreference, Long>