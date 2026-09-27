package com.erebuni782.app.ui.aerial

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erebuni782.app.AppGraph
import com.erebuni782.app.aerial.detect.DetectLevel
import com.erebuni782.app.aerial.detect.Detection
import com.erebuni782.app.aerial.detect.GridAnomalyDetector
import com.erebuni782.app.aerial.gen3d.Era
import com.erebuni782.app.aerial.gen3d.EraParametricGenerator
import com.erebuni782.app.aerial.gen3d.GltfWriter
import com.erebuni782.app.aerial.gen3d.ReconstructionInput
import com.erebuni782.app.aerial.stitch.BlendStitcher
import com.erebuni782.app.aerial.stitch.TileImage
import com.erebuni782.app.data.AerialMarkerUi
import com.erebuni782.app.data.AerialRepository
import com.erebuni782.app.data.AerialSessionUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

enum class MarkMode { MANUAL, AUTO }

data class AerialUiState(
    val session: AerialSessionUi? = null,
    val markers: List<AerialMarkerUi> = emptyList(),
    val mode: MarkMode = MarkMode.MANUAL,
    val level: DetectLevel = DetectLevel.WEAK,
    val busy: Boolean = false,
    val message: String? = null
)

class AerialViewModel(private val sessionId: String) : ViewModel() {

    private val repo: AerialRepository = AppGraph.aerial
    private val detector = GridAnomalyDetector()
    private val stitcher = BlendStitcher()

    private val sessionFlow = MutableStateFlow<AerialSessionUi?>(null)
    private val modeFlow = MutableStateFlow(MarkMode.MANUAL)
    private val levelFlow = MutableStateFlow(DetectLevel.WEAK)
    private val busyFlow = MutableStateFlow(false)
    private val messageFlow = MutableStateFlow<String?>(null)

    val state: StateFlow<AerialUiState> = combine(
        combine(
            sessionFlow,
            repo.markers(sessionId),
            modeFlow
        ) { session, markers, mode -> Triple(session, markers, mode) },
        combine(levelFlow, busyFlow) { level, busy -> level to busy },
        messageFlow
    ) { (session, markers, mode), (level, busy), message ->
        AerialUiState(session, markers.map { it.toUi() }, mode, level, busy, message)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AerialUiState())

    init {
        viewModelScope.launch { sessionFlow.value = repo.session(sessionId) }
    }

    fun setMode(mode: MarkMode) { modeFlow.value = mode }

    fun setLevel(level: DetectLevel) { levelFlow.value = level }

    /** Тап пальцем по фото — каждый тык = нумерованный маркер (ручной режим). */
    fun onImageTap(xNorm: Double, yNorm: Double) {
        if (modeFlow.value != MarkMode.MANUAL) return
        viewModelScope.launch {
            repo.addManualMarker(sessionId, xNorm, yNorm)
        }
    }

    fun deleteMarker(id: Long) {
        viewModelScope.launch { repo.deleteMarker(id) }
    }

    /** Демо-кадры из assets — офлайн-и e2e-проверка полного цикла. */
    fun loadDemoPhotos() {
        busy(true)
        viewModelScope.launch {
            listOf("aerial_demo/demo1.png", "aerial_demo/demo2.png").forEach { asset ->
                val target = File(repo.sessionDir(sessionId), File(asset).name)
                AppGraph.appContext.assets.open(asset).use { input ->
                    target.outputStream().use { input.copyTo(it) }
                }
                repo.addPhoto(sessionId, target.absolutePath)
            }
            sessionFlow.value = repo.session(sessionId)
            busy(false, messageFlow.value)
        }
    }

    /** Импорт из галереи: копия в приватное хранилище + EXIF-гео (D8). */
    fun importUris(uris: List<android.net.Uri>) {
        busy(true)
        viewModelScope.launch {
            var hasGeo = false
            uris.forEach { uri ->
                val target = File(repo.sessionDir(sessionId), "img_${System.nanoTime()}.jpg")
                runCatching {
                    AppGraph.appContext.contentResolver.openInputStream(uri)?.use { input ->
                        target.outputStream().use { input.copyTo(it) }
                    }
                }
                val latLon = readExifGeo(target)
                if (latLon != null) {
                    hasGeo = true
                    repo.setManualGeo(sessionId, latLon.first, latLon.second)
                }
                repo.addPhoto(sessionId, target.absolutePath)
            }
            if (hasGeo) repo.markExifGeo(sessionId)
            sessionFlow.value = repo.session(sessionId)
            busy(false)
        }
    }

    /** Сшивка с обязательным даунскейлом (AGENTS.md §10). */
    fun stitch() {
        busy(true)
        viewModelScope.launch(Dispatchers.Default) {
            val session = repo.session(sessionId) ?: return@launch
            val tiles = session.photos.mapNotNull { path -> loadDownscaled(path, MAX_DIM)?.toTile() }
            if (tiles.size < 2) {
                busy(false)
                return@launch
            }
            val result = stitcher.stitch(tiles)
            android.util.Log.d("P4DBG", "stitch done ${result.width}x${result.height}")
            val out = File(repo.sessionDir(sessionId), "stitched.png")
            val bmp = Bitmap.createBitmap(result.width, result.height, Bitmap.Config.ARGB_8888)
            bmp.setPixels(result.argb, 0, result.width, 0, 0, result.width, result.height)
            out.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bmp.recycle()
            repo.setStitched(sessionId, out.absolutePath)
            sessionFlow.value = repo.session(sessionId)
            busy(false)
        }
    }

    /** Автодетекция 3 уровней (D5). */
    fun detect() {
        busy(true)
        viewModelScope.launch(Dispatchers.Default) {
            val session = repo.session(sessionId) ?: return@launch
            val base = session.stitchedPath ?: session.photos.firstOrNull() ?: return@launch
            val bmp = loadDownscaled(base, MAX_DIM) ?: return@launch
            val w = bmp.width
            val h = bmp.height
            val argb = IntArray(w * h)
            bmp.getPixels(argb, 0, w, 0, 0, w, h)
            bmp.recycle()
            val real = detector.detect(w, h, argb, levelFlow.value)
            android.util.Log.d("P4DBG", "detect level=${levelFlow.value} found=${real.size}")
            withContext(Dispatchers.IO) {
                repo.clearAutoMarkers(sessionId)
                repo.addAutoMarkers(
                    sessionId,
                    real.map { it.x to it.y },
                    real.map { it.score }
                )
            }
            busy(false)
        }
    }

    fun setManualGeo(lat: Double, lon: Double) {
        viewModelScope.launch {
            repo.setManualGeo(sessionId, lat, lon)
            sessionFlow.value = repo.session(sessionId)
        }
    }

    fun exportGeoJson(onDone: (String) -> Unit) {
        viewModelScope.launch {
            val file = repo.exportGeoJson(sessionId)
            onDone(file.absolutePath)
        }
    }

    /** D6: параметрическая реконструкция → glTF файл. */
    fun generate3d(era: Era, year: Int, place: String, onDone: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            val contour = state.value.markers.takeIf { it.isNotEmpty() }
                ?.map { it.x to it.y }
                ?: listOf(0.1 to 0.1, 0.9 to 0.1, 0.9 to 0.9, 0.1 to 0.9)
            val parts = EraParametricGenerator.generate(ReconstructionInput(contour, era, year, place))
            val bytes = GltfWriter.writeGltf(parts)
            val out = File(repo.sessionDir(sessionId), "reconstruction_${era.name.lowercase()}.gltf")
            out.writeBytes(bytes)
            withContext(Dispatchers.Main) { onDone(out.absolutePath) }
        }
    }

    private fun busy(value: Boolean, keepMessage: String? = null) {
        busyFlow.value = value
        if (value) messageFlow.value = null else messageFlow.value = keepMessage
    }

    private fun readExifGeo(file: File): Pair<Double, Double>? = runCatching<Pair<Double, Double>?> {
        val exif = androidx.exifinterface.media.ExifInterface(file.absolutePath)
        val latLon = FloatArray(2)
        if (exif.getLatLong(latLon) && (latLon[0] != 0.0f || latLon[1] != 0.0f)) {
            latLon[0].toDouble() to latLon[1].toDouble()
        } else null
    }.getOrNull()

    private fun loadDownscaled(path: String, maxDim: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        var sample = 1
        while (bounds.outWidth / sample > maxDim || bounds.outHeight / sample > maxDim) sample *= 2
        return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })
    }

    private fun Bitmap.toTile() = TileImage(width, height, IntArray(width * height).also {
        getPixels(it, 0, width, 0, 0, width, height)
    })

    private fun com.erebuni782.app.data.db.AerialMarkerEntity.toUi() = AerialMarkerUi(
        id = id, number = number, x = x, y = y,
        manual = source == "MANUAL", confidence = confidence, note = note
    )

    companion object {
        const val MAX_DIM = 1024

        fun factory(sessionId: String) = viewModelFactory {
            initializer { AerialViewModel(sessionId) }
        }
    }
}
