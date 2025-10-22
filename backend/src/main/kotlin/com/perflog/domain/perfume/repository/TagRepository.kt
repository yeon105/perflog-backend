package com.perflog.domain.perfume.repository

import com.perflog.domain.perfume.model.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long>