package com.perflog.config.Kafka

import com.perflog.domain.perfume.model.document.PerfumeDocument
import com.perflog.domain.perfume.repository.PerfumeSearchRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PerfumeEventConsumer(private val perfumeSearchRepository: PerfumeSearchRepository) {
    @KafkaListener(
        topics = ["perfume-created"],
    )
    fun handleCreated(event: PerfumeCreatedEvent) {
        println("시작 ID: ${event.id}")

        val document = PerfumeDocument(
            id = event.id.toString(),
            name = event.name,
            brand = event.brand,
            launchYear = event.launchYear,
            season = event.season,
            gender = event.gender,
            notes = event.notes,
            tags = event.tags
        )
        
        // 실패 테스트
        if (event.id % 15 == 0L) {
            throw RuntimeException("실패 테스트")
        }

        perfumeSearchRepository.save(document)



        println("commit 완료 ID: ${event.id}")
    }
}