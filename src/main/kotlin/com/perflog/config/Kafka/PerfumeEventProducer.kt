package com.perflog.config.Kafka

import org.springframework.stereotype.Component

@Component
class PerfumeEventProducer(private val kafkaProducerConfig: KafkaProducerConfig) {

    fun sendCreatedEvent(event: PerfumeCreatedEvent) {
        kafkaProducerConfig.kafkaTemplate().send("perfume-created", event.id.toString(), event)
    }
}