package com.erebuni782.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сессия аэрофотосъёмки (P4). Смешанные источники (D8): если у фото нет
 * EXIF-гео — вручную задаём lat/lon на сессию.
 */
@Entity(tableName = "aerial_sessions")
data class AerialSessionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long,
    val photoPaths: String,          // исходники через \n
    val stitchedPath: String?,       // результат сшивки
    val geoLat: Double?,
    val geoLon: Double?,
    val hasExifGeo: Boolean
)

/**
 * Маркер на аэрофото. Координаты НОРМИРОВАННЫЕ (0..1) — не зависят от
 * разрешения. D7: registryEntryId зарезервировано, но выключено (nullable,
 * UI не использует).
 */
@Entity(tableName = "aerial_markers")
data class AerialMarkerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val number: Int,
    val x: Double,
    val y: Double,
    val source: String,              // MANUAL / AUTO
    val confidence: Double?,
    val note: String,
    val registryEntryId: String? = null   // D7: резерв, пока выключено
)
