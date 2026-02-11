package com.perflog.domain.perfume.model.entity

import jakarta.persistence.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

@Document(indexName = "perfumes")
data class PerfumeDocument(
    @Id
    val id: String? = null,                 // Elasticsearch _id
    @Field(type = FieldType.Text)
    val name: String,
    @Field(type = FieldType.Text)
    val brand: String
)


