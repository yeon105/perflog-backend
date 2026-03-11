package com.perflog.domain.perfume.model.document

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.perfume.model.entity.Tag
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.Setting

@Document(indexName = "perfumes")
@JsonIgnoreProperties(ignoreUnknown = true)
@Setting(settingPath = "elasticsearch/perfume-settings.json")
data class PerfumeDocument(

    @Id
    val id: String,

    @Field(
        type = FieldType.Text,
        analyzer = "autocomplete_analyzer",
        searchAnalyzer = "autocomplete_search_analyzer"
    )
    val name: String,

    @Field(type = FieldType.Text)
    val brand: String,

    @Field(type = FieldType.Integer)
    val launchYear: Int?,

    @Field(type = FieldType.Keyword)
    val season: String?,

    @Field(type = FieldType.Keyword)
    val gender: String?,

    @Field(type = FieldType.Text)
    val notes: String?,

    @Field(type = FieldType.Keyword)
    val tags: List<String>,


    ) {
    companion object {

        fun of(
            perfume: Perfume,
            tags: List<Tag>
        ): PerfumeDocument {

            return PerfumeDocument(
                id = perfume.id.toString(),
                name = perfume.name,
                brand = perfume.brand,
                launchYear = perfume.launchYear,
                season = perfume.season.name,
                gender = perfume.gender.name,
                notes = listOfNotNull(
                    perfume.topNotes,
                    perfume.middleNotes,
                    perfume.baseNotes
                ).joinToString(" "),
                tags = tags.map { it.name }
            )
        }
    }
}