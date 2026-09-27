package com.erebuni782.app.export

import android.content.Context
import com.erebuni782.app.data.db.AerialMarkerEntity
import com.erebuni782.app.data.db.AerialSessionDao
import com.erebuni782.app.data.db.AerialMarkerDao
import com.erebuni782.app.data.db.ArtifactDao
import com.erebuni782.app.data.db.ArtifactEntity
import com.erebuni782.app.data.db.AuditDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.SecureRandom
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.crypto.Mac
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

data class ExportResult(val artifacts: Int, val photos: Int, val file: File)
data class ImportResult(val artifacts: Int, val aerialSessions: Int, val verified: Boolean)

/**
 * D2: подписанный файл экспорта/импорта для ПК — zip (.e782):
 *   data.json (артефакты+аудит+аэро) + папка photos + manifest.json + signature.bin
 * Подпись: HMAC-SHA256 по data.json; ключ = PBKDF2(парольная фраза, salt).
 * Импорт: проверка подписи → upsert (id-стабильность, синк-совместимо).
 */
class ExportRepository(
    private val context: Context,
    private val artifactDao: ArtifactDao,
    private val auditDao: AuditDao,
    private val sessionDao: AerialSessionDao,
    private val markerDao: AerialMarkerDao
) {

    fun exportsDir(): File =
        (context.getExternalFilesDir("exports") ?: java.io.File(context.filesDir, "exports"))
            .apply { mkdirs() }

    suspend fun exportTo(file: File, passphrase: String): ExportResult = withContext(Dispatchers.IO) {
        val artifacts = mutableListOf<ArtifactEntity>()
        artifactDao.observeActive().first().forEach { artifacts += it }
        val audit = mutableListOf<com.erebuni782.app.data.db.AuditEntryEntity>()
        // полный аудит: по всем артефактам экспорта
        val data = JSONObject()
        data.put("format", "erebuni782-export")
        data.put("version", 1)
        data.put("exportedAt", System.currentTimeMillis())
        data.put("deviceId", com.erebuni782.app.sync.DeviceId.get(context))
        data.put("artifacts", JSONArray(artifacts.map { entityToJson(it) }))
        val auditArray = JSONArray()
        artifacts.forEach { a ->
            auditDao.observeForArtifact(a.id).first().forEach { auditArray.put(entryToJson(it)) }
        }
        data.put("audit", auditArray)
        val sessions = sessionDao.observeAll().first()
        data.put("aerialSessions", JSONArray(sessions.map { entityToJson(it) }))
        val markersArray = JSONArray()
        sessions.forEach { s ->
            markerDao.observeForSession(s.id).first().forEach { markersArray.put(entryToJson(it)) }
        }
        data.put("aerialMarkers", markersArray)

        val dataBytes = data.toString().toByteArray(Charsets.UTF_8)
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val key = deriveKey(passphrase, salt)
        val signature = hmac(key, dataBytes)

        var photoCount = 0
        ZipOutputStream(file.outputStream().buffered()).use { zip ->
            put(zip, "data.json", dataBytes)
            put(zip, "manifest.json", JSONObject()
                .put("alg", "PBKDF2-HMAC-SHA256")
                .put("iterations", ITERATIONS)
                .put("salt", salt.toHex())
                .put("artifactCount", artifacts.size)
                .toString().toByteArray())
            put(zip, "signature.bin", signature)
            artifacts.forEach { a ->
                a.photoPaths.lines().filter { it.isNotBlank() }.forEach { path ->
                    val f = File(path)
                    if (f.exists()) {
                        put(zip, "photos/${a.id}/${f.name}", f.readBytes())
                        photoCount++
                    }
                }
            }
        }
        ExportResult(artifacts.size, photoCount, file)
    }

    suspend fun importFrom(file: File, passphrase: String): ImportResult = withContext(Dispatchers.IO) {
        require(file.exists()) { "файл не найден" }
        ZipFile(file).use { zip ->
            val dataBytes = zip.getEntry("data.json")?.let { zip.getInputStream(it).readBytes() }
                ?: error("нет data.json")
            val manifest = JSONObject(
                zip.getEntry("manifest.json")?.let { zip.getInputStream(it).readBytes()?.toString(Charsets.UTF_8) }
                    ?: error("нет manifest.json")
            )
            val signature = zip.getEntry("signature.bin")?.let { zip.getInputStream(it).readBytes() }
                ?: error("нет signature.bin")

            val salt = hexToBytes(manifest.getString("salt"))
            val key = deriveKey(passphrase, salt)
            val expected = hmac(key, dataBytes)
            check(java.security.MessageDigest.isEqual(expected, signature)) {
                "ПОДПИСЬ НЕ СОВПАЛА: неверная парольная фраза или файл повреждён"
            }

            val data = JSONObject(dataBytes.toString(Charsets.UTF_8))
            val artifacts = data.getJSONArray("artifacts")
            for (i in 0 until artifacts.length()) {
                val e = jsonToArtifact(artifacts.getJSONObject(i))
                // фото из архива → приватное хранилище
                val restored = e.photoPaths.lines().filter { it.isNotBlank() }.map { p ->
                    val name = File(p).name
                    val entry = zip.getEntry("photos/${e.id}/$name")
                    if (entry != null) {
                        val target = File(context.filesDir, "artifacts/${e.id}/$name").apply {
                            parentFile?.mkdirs()
                        }
                        target.outputStream().use { zip.getInputStream(entry).copyTo(it) }
                        target.absolutePath
                    } else p
                }
                artifactDao.upsert(e.copy(photoPaths = restored.joinToString("\n")))
            }
            val audit = data.optJSONArray("audit") ?: JSONArray()
            for (i in 0 until audit.length()) {
                auditDao.insert(jsonToEntry(audit.getJSONObject(i)))
            }
            val sessions = data.optJSONArray("aerialSessions") ?: JSONArray()
            for (i in 0 until sessions.length()) {
                sessionDao.upsert(jsonToSession(sessions.getJSONObject(i)))
            }
            val markers = data.optJSONArray("aerialMarkers") ?: JSONArray()
            for (i in 0 until markers.length()) {
                markerDao.insert(jsonToMarker(markers.getJSONObject(i)))
            }
            ImportResult(artifacts.length(), sessions.length(), verified = true)
        }
    }

    fun latestExport(): File? =
        exportsDir().listFiles { f -> f.extension == "e782" }?.maxByOrNull { it.lastModified() }

    private fun put(zip: ZipOutputStream, name: String, bytes: ByteArray) {
        zip.putNextEntry(ZipEntry(name))
        zip.write(bytes)
        zip.closeEntry()
    }

    private fun deriveKey(passphrase: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, ITERATIONS, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    private fun hmac(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data)
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
    private fun hexToBytes(hex: String): ByteArray =
        ByteArray(hex.length / 2) { ((Character.digit(hex[it * 2], 16) shl 4) + Character.digit(hex[it * 2 + 1], 16)).toByte() }

    companion object {
        const val ITERATIONS = 50_000
    }
}

// ── JSON-мапперы (entity ↔ json, все поля) ──

internal fun entityToJson(e: ArtifactEntity): JSONObject = JSONObject()
    .put("id", e.id).put("category", e.category).put("title", e.title)
    .put("description", e.description)
    .put("latitude", e.latitude ?: JSONObject.NULL).put("longitude", e.longitude ?: JSONObject.NULL)
    .put("address", e.address).put("discoveryEpochDay", e.discoveryEpochDay ?: JSONObject.NULL)
    .put("custodian", e.custodian).put("custodyStatus", e.custodyStatus)
    .put("nextInspectionEpochDay", e.nextInspectionEpochDay ?: JSONObject.NULL)
    .put("photoPaths", e.photoPaths).put("createdAt", e.createdAt).put("updatedAt", e.updatedAt)
    .put("deleted", e.deleted).put("versionVector", e.versionVector).put("lastEditor", e.lastEditor)

internal fun jsonToArtifact(o: JSONObject): ArtifactEntity = ArtifactEntity(
    id = o.getString("id"), category = o.getString("category"), title = o.getString("title"),
    description = o.optString("description", ""),
    latitude = if (o.isNull("latitude")) null else o.getDouble("latitude"),
    longitude = if (o.isNull("longitude")) null else o.getDouble("longitude"),
    address = o.optString("address", ""),
    discoveryEpochDay = if (o.isNull("discoveryEpochDay")) null else o.getLong("discoveryEpochDay"),
    custodian = o.optString("custodian", ""), custodyStatus = o.getString("custodyStatus"),
    nextInspectionEpochDay = if (o.isNull("nextInspectionEpochDay")) null else o.getLong("nextInspectionEpochDay"),
    photoPaths = o.optString("photoPaths", ""), createdAt = o.getLong("createdAt"), updatedAt = o.getLong("updatedAt"),
    deleted = o.optBoolean("deleted", false),
    versionVector = o.optString("versionVector", "{}"), lastEditor = o.optString("lastEditor", "")
)

internal fun entryToJson(e: com.erebuni782.app.data.db.AuditEntryEntity): JSONObject = JSONObject()
    .put("artifactId", e.artifactId).put("timestamp", e.timestamp)
    .put("action", e.action).put("details", e.details)

internal fun jsonToEntry(o: JSONObject) = com.erebuni782.app.data.db.AuditEntryEntity(
    artifactId = o.getString("artifactId"), timestamp = o.getLong("timestamp"),
    action = o.getString("action"), details = o.optString("details", "")
)

internal fun entityToJson(s: com.erebuni782.app.data.db.AerialSessionEntity): JSONObject = JSONObject()
    .put("id", s.id).put("title", s.title).put("createdAt", s.createdAt)
    .put("photoPaths", s.photoPaths).put("stitchedPath", s.stitchedPath ?: JSONObject.NULL)
    .put("geoLat", s.geoLat ?: JSONObject.NULL).put("geoLon", s.geoLon ?: JSONObject.NULL)
    .put("hasExifGeo", s.hasExifGeo)

internal fun jsonToSession(o: JSONObject) = com.erebuni782.app.data.db.AerialSessionEntity(
    id = o.getString("id"), title = o.getString("title"), createdAt = o.getLong("createdAt"),
    photoPaths = o.optString("photoPaths", ""),
    stitchedPath = if (o.isNull("stitchedPath")) null else o.getString("stitchedPath"),
    geoLat = if (o.isNull("geoLat")) null else o.getDouble("geoLat"),
    geoLon = if (o.isNull("geoLon")) null else o.getDouble("geoLon"),
    hasExifGeo = o.optBoolean("hasExifGeo", false)
)

internal fun entryToJson(m: AerialMarkerEntity): JSONObject = JSONObject()
    .put("sessionId", m.sessionId).put("number", m.number)
    .put("x", m.x).put("y", m.y).put("source", m.source)
    .put("confidence", m.confidence ?: JSONObject.NULL).put("note", m.note)

internal fun jsonToMarker(o: JSONObject) = AerialMarkerEntity(
    sessionId = o.getString("sessionId"), number = o.getInt("number"),
    x = o.getDouble("x"), y = o.getDouble("y"), source = o.getString("source"),
    confidence = if (o.isNull("confidence")) null else o.getDouble("confidence"),
    note = o.optString("note", "")
)
