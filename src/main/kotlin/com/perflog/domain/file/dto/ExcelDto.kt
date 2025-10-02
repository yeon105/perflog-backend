package com.perflog.domain.file.dto

class ExcelDto {

    /**
     * 업로드 결과
     * - 성공/실패 카운트 및 실패 행의 간단한 이유를 담아 반환
     */
    data class UploadResult(
        val total: Int, // 업로드 시도한 전체 행 개수 (header 제외)
        val success: Int, // DB에 정상적으로 저장된 행 개수
        val failed: Int, // 검증 오류 또는 저장 실패로 인해 반영되지 못한 행 개수
        val failedRows: List<FailedRow> // 실패한 행 정보
    )

    data class FailedRow(
        val rowIndex: Int,  // 엑셀 row 번호
        val reason: String  // 실패 이유
    )
}
