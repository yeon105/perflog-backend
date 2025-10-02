package com.perflog.domain.file.service

import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.domain.file.dto.ExcelDto
import com.perflog.domain.file.model.FileUpload
import com.perflog.domain.file.repository.FileUploadRepository
import com.perflog.domain.member.repository.MemberRepository
import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.perfume.model.entity.PerfumeTag
import com.perflog.domain.perfume.model.enum.Gender
import com.perflog.domain.perfume.model.enum.Longevity
import com.perflog.domain.perfume.model.enum.Season
import com.perflog.domain.perfume.repository.PerfumeRepository
import com.perflog.domain.perfume.repository.PerfumeTagRepository
import com.perflog.domain.perfume.repository.TagRepository
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Transactional
@Service
class ExcelServiceImpl(
    private val perfumeRepository: PerfumeRepository,
    private val tagRepository: TagRepository,
    private val perfumeTagRepository: PerfumeTagRepository,
    private val fileUploadRepository: FileUploadRepository,
    private val memberRepository: MemberRepository,
) : ExcelService {

    private val log = LoggerFactory.getLogger(ExcelServiceImpl::class.java)

    override fun importPerfumes(
        file: MultipartFile,
        authentication: Authentication
    ): ExcelDto.UploadResult {
        if (file.isEmpty) throw CustomException(ErrorCode.INVALID_ARGUMENT)

        val member = memberRepository.findByEmail(authentication.name)
            ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND)

        var total = 0
        var success = 0
        val failedRows = mutableListOf<ExcelDto.FailedRow>()

        val tagsByName = tagRepository.findAll().associateBy { it.name.trim() }

        WorkbookFactory.create(file.inputStream).use { wb ->
            val sheet = wb.getSheetAt(0)

            for (i in 1..sheet.lastRowNum) {
                val row = sheet.getRow(i) ?: continue
                total++

                try {
                    val name = cellString(row, 0)
                    val brand = cellString(row, 1)
                    val launchYear = cellString(row, 2).toInt()
                    val imageUrl = cellString(row, 3)

                    val longevity =
                        Longevity.valueOf(cellString(row, 4).trim().uppercase())
                    val season =
                        Season.valueOf(cellString(row, 5).trim().uppercase())
                    val gender =
                        Gender.valueOf(cellString(row, 6).trim().uppercase())

                    val topNotes = splitNotes(cellString(row, 7))
                    val middleNotes = splitNotes(cellString(row, 8))
                    val baseNotes = splitNotes(cellString(row, 9))
                    val tagNames = parseTagNames(cellString(row, 10))

                    if (name.isBlank() || brand.isBlank() ||
                        launchYear.toString().isBlank() || imageUrl.isBlank() ||
                        longevity.toString().isBlank() || season.toString()
                            .isBlank() || gender.toString().isBlank() ||
                        topNotes.isNullOrBlank() || middleNotes.isNullOrBlank() || baseNotes.isNullOrBlank()
                    ) {
                        log.warn("Row {} skipped: required field missing", i)
                        failedRows.add(ExcelDto.FailedRow(i, "필수 필드 누락"))
                        continue
                    }

                    if (perfumeRepository.existsByNameAndBrand(name, brand)) {
                        failedRows.add(ExcelDto.FailedRow(i, "중복된 향수"))
                        continue
                    }

                    val perfume = perfumeRepository.save(
                        Perfume(
                            name = name,
                            brand = brand,
                            launchYear = launchYear,
                            imageUrl = imageUrl,
                            longevity = longevity,
                            season = season,
                            gender = gender,
                            topNotes = topNotes,
                            middleNotes = middleNotes,
                            baseNotes = baseNotes
                        )
                    )

                    if (tagNames.isNotEmpty()) {
                        val names = tagNames.map { it.trim() }.filter { it.isNotBlank() }.toSet()
                        val existing = names.mapNotNull { tagsByName[it] }
                        val missing = names - existing.map { it.name }.toSet()
                        if (existing.isNotEmpty()) {
                            perfumeTagRepository.saveAll(
                                existing.map { PerfumeTag(perfume, it) }
                            )
                        }
                        if (missing.isNotEmpty()) {
                            log.warn("Row {}: missing tags {}", i, missing)
                        }
                    }

                    success++
                } catch (e: Exception) {
                    log.warn("Row {} import failed: {}", i, e.message)
                    failedRows.add(ExcelDto.FailedRow(i, "예외 발생: ${e.message}"))
                    continue
                }
            }
        }

        fileUploadRepository.save(
            FileUpload(
                member, file.originalFilename.toString()
            )
        )

        return ExcelDto.UploadResult(
            total = total,
            success = success,
            failed = total - success,
            failedRows = failedRows
        )
    }

    private fun cellString(row: Row, idx: Int): String {
        val cell = row.getCell(idx) ?: return ""
        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue.trim()
            CellType.NUMERIC -> cell.numericCellValue.toLong().toString()
            else -> ""
        }
    }

    private fun splitNotes(s: String?): String? {
        return s?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.takeIf { it.isNotEmpty() }
            ?.joinToString(", ")
    }

    private fun parseTagNames(s: String?): Set<String> =
        s?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.toSet()
            ?: emptySet()
}
