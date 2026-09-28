package com.erebuni782.app.data

import com.erebuni782.app.data.db.ArtifactDao
import com.erebuni782.app.data.db.ArtifactEntity
import com.erebuni782.app.data.db.AuditDao
import com.erebuni782.app.data.db.AuditEntryEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class ArtifactUi(
    val id: String,
    val category: ArtifactCategory,
    val title: String,
    val description: String,
    val latitude: Double?,
    val longitude: Double?,
    val address: String,
    val discoveryEpochDay: Long?,
    val custodian: String,
    val custodyStatus: CustodyStatus,
    val nextInspectionEpochDay: Long?,
    val photoPaths: List<String>,
    val createdAt: Long,
    val updatedAt: Long,
    val versionVector: String = "{}",
    val lastEditor: String = ""
)

interface ArtifactStore {
    fun observeActive(): Flow<List<ArtifactEntity>>
    suspend fun byId(id: String): ArtifactEntity?
    suspend fun create(ui: ArtifactUi): String
    suspend fun update(ui: ArtifactUi, action: String, details: String)
    suspend fun setStatus(id: String, status: CustodyStatus)
    suspend fun addPhoto(id: String, path: String)
    suspend fun softDelete(id: String)
    fun auditFor(id: String): Flow<List<AuditEntryEntity>>
}

/**
 * Реестр артефактов + обязательный аудит-лог каждого изменения (спека §1).
 * Soft-delete — задел под синк P5.
 */
class ArtifactRepository(
    private val artifactDao: ArtifactDao,
    private val auditDao: AuditDao,
    private val clock: () -> Long = { System.currentTimeMillis() },
    private val deviceIdProvider: () -> String = { "" }
) : ArtifactStore {

    private fun bumpVv(raw: String): Pair<String, String> {
        val deviceId = deviceIdProvider()
        val vv = org.json.JSONObject(raw)
        vv.put(deviceId, vv.optLong(deviceId, 0L) + 1L)
        return vv.toString() to deviceId
    }

    override fun observeActive(): Flow<List<ArtifactEntity>> = artifactDao.observeActive()

    override suspend fun byId(id: String): ArtifactEntity? = artifactDao.byId(id)

    override suspend fun create(ui: ArtifactUi): String {
        val id = ui.id.ifEmpty { UUID.randomUUID().toString() }
        val now = clock()
        val (vv, editor) = bumpVv("{}")
        artifactDao.upsert(ui.toEntity(id, now, now).copy(versionVector = vv, lastEditor = editor))
        auditDao.insert(
            AuditEntryEntity(artifactId = id, timestamp = now, action = ACTION_CREATED, details = "category=${ui.category}")
        )
        return id
    }

    override suspend fun update(ui: ArtifactUi, action: String, details: String) {
        val now = clock()
        val (vv, editor) = bumpVv(ui.versionVector)
        artifactDao.upsert(ui.toEntity(ui.id, ui.createdAt, now).copy(versionVector = vv, lastEditor = editor))
        auditDao.insert(AuditEntryEntity(artifactId = ui.id, timestamp = now, action = action, details = details))
    }

    override suspend fun setStatus(id: String, status: CustodyStatus) {
        val current = artifactDao.byId(id) ?: return
        val now = clock()
        val from = CustodyStatus.fromRaw(current.custodyStatus)
        if (from == status) return
        require(CustodyStatus.canTransition(from, status)) {
            "Недопустимый переход статуса: $from -> $status"
        }
        val (vv, editor) = bumpVv(current.versionVector)
        artifactDao.upsert(current.copy(custodyStatus = status.name, updatedAt = now, versionVector = vv, lastEditor = editor))
        auditDao.insert(
            AuditEntryEntity(
                artifactId = id, timestamp = now,
                action = ACTION_STATUS_CHANGED, details = "$from -> $status"
            )
        )
    }

    override suspend fun addPhoto(id: String, path: String) {
        val current = artifactDao.byId(id) ?: return
        val now = clock()
        val photos = if (current.photoPaths.isBlank()) path else current.photoPaths + "\n" + path
        val (vv, editor) = bumpVv(current.versionVector)
        artifactDao.upsert(current.copy(photoPaths = photos, updatedAt = now, versionVector = vv, lastEditor = editor))
        auditDao.insert(
            AuditEntryEntity(artifactId = id, timestamp = now, action = ACTION_PHOTO_ADDED, details = path)
        )
    }

    override suspend fun softDelete(id: String) {
        val current = artifactDao.byId(id) ?: return
        val now = clock()
        val (vv, editor) = bumpVv(current.versionVector)
        artifactDao.upsert(current.copy(deleted = true, updatedAt = now, versionVector = vv, lastEditor = editor))
        auditDao.insert(
            AuditEntryEntity(artifactId = id, timestamp = now, action = ACTION_DELETED, details = "")
        )
    }

    override fun auditFor(id: String): Flow<List<AuditEntryEntity>> = auditDao.observeForArtifact(id)

    private fun ArtifactUi.toEntity(id: String, createdAt: Long, updatedAt: Long) = ArtifactEntity(
        id = id,
        category = category.name,
        title = title,
        description = description,
        latitude = latitude,
        longitude = longitude,
        address = address,
        discoveryEpochDay = discoveryEpochDay,
        custodian = custodian,
        custodyStatus = custodyStatus.name,
        nextInspectionEpochDay = nextInspectionEpochDay,
        photoPaths = photoPaths.joinToString("\n"),
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        const val ACTION_CREATED = "CREATED"
        const val ACTION_UPDATED = "UPDATED"
        const val ACTION_STATUS_CHANGED = "STATUS_CHANGED"
        const val ACTION_PHOTO_ADDED = "PHOTO_ADDED"
        const val ACTION_DELETED = "DELETED"

        fun toUi(e: ArtifactEntity) = ArtifactUi(
            id = e.id,
            category = ArtifactCategory.fromRaw(e.category),
            title = e.title,
            description = e.description,
            latitude = e.latitude,
            longitude = e.longitude,
            address = e.address,
            discoveryEpochDay = e.discoveryEpochDay,
            custodian = e.custodian,
            custodyStatus = CustodyStatus.fromRaw(e.custodyStatus),
            nextInspectionEpochDay = e.nextInspectionEpochDay,
            photoPaths = e.photoPaths.lines().filter { it.isNotBlank() },
            createdAt = e.createdAt,
            updatedAt = e.updatedAt,
            versionVector = e.versionVector,
            lastEditor = e.lastEditor
        )
    }
}
