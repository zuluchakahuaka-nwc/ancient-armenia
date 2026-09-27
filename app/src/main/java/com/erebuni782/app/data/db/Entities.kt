package com.erebuni782.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wiki_articles")
data class WikiArticleEntity(
    @PrimaryKey val id: String,
    val category: String,
    val sortOrder: Int,
    val titleEn: String,
    val titleRu: String,
    val titleHy: String,
    val bodyEn: String,
    val bodyRu: String,
    val bodyHy: String
)

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleRu: String,
    val titleHy: String,
    val authorEn: String,
    val authorRu: String,
    val authorHy: String,
    val licenseNote: String
)

@Entity(tableName = "chapters", primaryKeys = ["bookId", "chapterIndex"])
data class ChapterEntity(
    val bookId: String,
    val chapterIndex: Int,
    val titleEn: String,
    val titleRu: String,
    val titleHy: String,
    val bodyEn: String,
    val bodyRu: String,
    val bodyHy: String
)

@Entity(tableName = "audio_tracks")
data class AudioTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val kind: String,
    val uri: String?,
    val titleEn: String,
    val titleRu: String,
    val titleHy: String,
    val durationSec: Int?
)
