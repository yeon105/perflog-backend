package com.perflog.domain.file.service

import com.perflog.domain.file.dto.ExcelDto
import org.springframework.security.core.Authentication
import org.springframework.web.multipart.MultipartFile

interface ExcelService {

    /**
     * Excel 파일을 업로드하여 향수 데이터를 DB에 저장한다.
     * 업로드 이력은 file_uploads 테이블에도 기록된다.
     *
     * @param file 업로드할 Excel 파일 (XLSX 형식)
     * @param authentication 업로드를 수행한 사용자 정보
     * @return 업로드 결과 (총 건수, 성공 건수, 실패 건수, 에러 메시지 목록)
     */
    fun importPerfumes(file: MultipartFile, authentication: Authentication): ExcelDto.UploadResult
}