package com.erebuni782.app.data

import android.content.Context
import com.erebuni782.app.data.db.AerialMarkerDao
import com.erebuni782.app.data.db.AerialMarkerEntity
import com.erebuni782.app.data.db.AerialSessionDao
import com.erebuni782.app.data.db.AerialSessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

data class AerialMarkerUi(
    val id: Long,
    val number: Int,
    val x: Double,
    val y: Double,
    val manual: Boolean,
    val confidence: Double?,
    val note: String
)

data class AerialSessionUi(
    val id: String,
    val title: String,
    val createdAt: Long,
    val photos: List<String>,
    val stitchedPath: String?,
    val geoLat: Double?,
    val geoLon: Double?,
    val hasExifGeo: Boolean
)

/**
 * Аэро-разведка P4. D7: разметка — ОТДЕЛЬНОЕ хранилище; поле связи
 * с реестром (registryEntryId) зарезервировано в схеме, но выключено.
 */
class AerialRepository(
    private val context: Context,
    private val sessionDao: AerialSessionDao,
    private val markerDao: AerialMarkerDao
) {

    fun sessions(): Flow<List<AerialSessionEntity>> = sessionDao.observeAll()

    fun markers(sessionId: String): Flow<List<AerialMarkerEntity>> = markerDao.observeForSession(sessionId)

    suspend fun session(id: String): AerialSessionUi? = sessionDao.byId(id)?.toUi()

    suspend fun createSession(title: String): AerialSessionUi {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        sessionDao.upsert(
            AerialSessionEntity(
                id = id, title = title, createdAt = now,
                photoPaths = "", stitchedPath = null,
                geoLat = null, geoLon = null, hasExifGeo = false
            )
        )
        return AerialSessionUi(id, title, now, emptyList(), null, null, null, false)
    }

    suspend fun addPhoto(sessionId: String, path: String) {
        val s = sessionDao.byId(sessionId) ?: return
        val photos = if (s.photoPaths.isBlank()) path else s.photoPaths + "\n" + path
        sessionDao.upsert(s.copy(photoPaths = photos))
    }

    suspend fun setStitched(sessionId: String, path: String) {
        val s = sessionDao.byId(sessionId) ?: return
        sessionDao.upsert(s.copy(stitchedPath = path))
    }

    /** D8: ручная геопривязка при отсутствии EXIF-гео. */
    suspend fun setManualGeo(sessionId: String, lat: Double, lon: Double) {
        val s = sessionDao.byId(sessionId) ?: return
        sessionDao.upsert(s.copy(geoLat = lat, geoLon = lon))
    }

    suspend fun markExifGeo(sessionId: String) {
        val s = sessionDao.byId(sessionId) ?: return
        sessionDao.upsert(s.copy(hasExifGeo = true))
    }

    suspend fun addManualMarker(sessionId: String, x: Double, y: Double): AerialMarkerEntity {
        val number = (markerDao.maxNumber(sessionId) ?: 0) + 1
        val marker = AerialMarkerEntity(
            sessionId = sessionId, number = number,
            x = x, y = y, source = "MANUAL", confidence = null, note = ""
        )
        val id = markerDao.insert(marker)
        return marker.copy(id = id)
    }

    suspend fun addAutoMarkers(sessionId: String, detections: List<Pair<Double, Double>>, confidences: List<Double>) {
        var number = (markerDao.maxNumber(sessionId) ?: 0)
        detections.forEachIndexed { i, (x, y) ->
            number++
            markerDao.insert(
                AerialMarkerEntity(
                    sessionId = sessionId, number = number,
                    x = x, y = y, source = "AUTO",
                    confidence = confidences.getOrElse(i) { 0.0 }, note = ""
                )
            )
        }
    }

    suspend fun clearAutoMarkers(sessionId: String) {
        markerDao.forSession(sessionId).filter { it.source == "AUTO" }.forEach {
            markerDao.delete(it.id)
        }
    }

    suspend fun deleteMarker(markerId: Long) = markerDao.delete(markerId)

    /** Новые файлы сессии: filesDir/aerial/<sessionId>/ */
    fun sessionDir(sessionId: String): File =
        File(context.filesDir, "aerial/$sessionId").apply { mkdirs() }

    suspend fun exportGeoJson(sessionId: String): File = withContext(Dispatchers.IO) {
        val s = sessionDao.byId(sessionId)!!
        val markers = markerDao.forSession(sessionId)
        val json = com.erebuni782.app.aerial.geo.GeoJsonExporter.export(
            sessionTitle = s.title,
            lat = s.geoLat,
            lon = s.geoLon,
            markers = markers.map { com.erebuni782.app.aerial.geo.GeoJsonExporter.MarkerPoint(it.number, it.x, it.y, it.source, it.confidence) }
        )
        val file = File(sessionDir(sessionId), "markers.geojson")
        file.writeText(json)
        file
    }

    private fun AerialSessionEntity.toUi() = AerialSessionUi(
        id = id, title = title, createdAt = createdAt,
        photos = photoPaths.lines().filter { it.isNotBlank() },
        stitchedPath = stitchedPath, geoLat = geoLat, geoLon = geoLon,
        hasExifGeo = hasExifGeo
    )
}
