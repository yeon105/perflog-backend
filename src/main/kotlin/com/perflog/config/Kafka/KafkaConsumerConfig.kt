package com.perflog.config.Kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConsumerConfig {
    @Bean
    fun consumerFactory(): ConsumerFactory<String, PerfumeCreatedEvent> {

        val deserializer = JsonDeserializer(PerfumeCreatedEvent::class.java)
        deserializer.addTrustedPackages("*")

        val config = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG to "perfume-group",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            //   autoCommit 수정
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to deserializer
        )

        return DefaultKafkaConsumerFactory(config, StringDeserializer(), deserializer)
    }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, PerfumeCreatedEvent>,
        kafkaTemplate: KafkaTemplate<String, Any>
    ): ConcurrentKafkaListenerContainerFactory<String, PerfumeCreatedEvent> {

        val factory =
            ConcurrentKafkaListenerContainerFactory<String, PerfumeCreatedEvent>()

        factory.consumerFactory = consumerFactory
        // MANUAL
        factory.containerProperties.ackMode =
            ContainerProperties.AckMode.RECORD

        // DLQ  재시도 설정
        val recoverer = DeadLetterPublishingRecoverer(kafkaTemplate)
        val backOff = FixedBackOff(1000L, 9) //

        factory.setCommonErrorHandler(DefaultErrorHandler(recoverer, backOff))

        return factory
    }
}