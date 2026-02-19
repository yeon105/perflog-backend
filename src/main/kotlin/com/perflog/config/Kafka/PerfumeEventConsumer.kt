package com.perflog.config.Kafka

import com.perflog.domain.perfume.model.document.PerfumeDocument
import com.perflog.domain.perfume.repository.PerfumeSearchRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PerfumeEventConsumer(private val perfumeSearchRepository: PerfumeSearchRepository) {
    @KafkaListener(
        topics = ["perfume-created"],
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun handleCreated(event: PerfumeCreatedEvent) {

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

        perfumeSearchRepository.save(document)
    }
}