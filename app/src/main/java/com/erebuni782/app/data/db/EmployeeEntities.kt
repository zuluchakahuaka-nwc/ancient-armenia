package com.erebuni782.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Запись реестра артефактов (режим сотрудника, P3). */
@Entity(tableName = "artifacts")
data class ArtifactEntity(
    @PrimaryKey val id: String,
    val category: String,        // ArtifactCategory.name
    val title: String,
    val description: String,
    val latitude: Double?,
    val longitude: Double?,
    val address: String,
    val discoveryEpochDay: Long?,
    val custodian: String,
    val custodyStatus: String,   // CustodyStatus.name
    val nextInspectionEpochDay: Long?,
    val photoPaths: String,      // пути через \n
    val createdAt: Long,
    val updatedAt: Long,
    val deleted: Boolean = false // soft-delete для будущего синка (P5)
)

/** Аудит-лог: каждое изменение записи фиксируется (спека §1). */
@Entity(tableName = "audit_log")
data class AuditEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val artifactId: String,
    val timestamp: Long,
    val action: String,          // CREATED / UPDATED / STATUS_CHANGED / PHOTO_ADDED / DELETED
    val details: String
)
