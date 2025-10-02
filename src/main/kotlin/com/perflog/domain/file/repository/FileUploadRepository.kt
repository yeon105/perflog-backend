package com.perflog.domain.file.repository

import com.perflog.domain.file.model.FileUpload
import org.springframework.data.jpa.repository.JpaRepository

interface FileUploadRepository : JpaRepository<FileUpload, Long>
