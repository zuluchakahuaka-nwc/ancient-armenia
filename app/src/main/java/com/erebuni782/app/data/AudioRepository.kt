package com.erebuni782.app.data

import com.erebuni782.app.data.db.AudioTrackDao
import com.erebuni782.app.data.db.AudioTrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

data class UserTrackUi(val id: Long, val uri: String, val title: String)

data class GuideTrackUi(val id: Long, val title: String, val durationSec: Int, val available: Boolean)

data class StationTrack(val assetPath: String, val titleRes: Int)

/** Urartu.fm: офлайн-пакет треков идёт с приложением (D10). */
val URARTU_FM_PACK: List<StationTrack> = listOf(
    StationTrack("urartu_fm/track_aran_berd.wav", com.erebuni782.app.R.string.station_track_aran_berd),
    StationTrack("urartu_fm/track_haldi_temple.wav", com.erebuni782.app.R.string.station_track_haldi_temple),
    StationTrack("urartu_fm/track_karmir_blur.wav", com.erebuni782.app.R.string.station_track_karmir_blur)
)

interface AudioStore {
    fun userTracks(localeTag: String): Flow<List<UserTrackUi>>
    fun guides(localeTag: String): Flow<List<GuideTrackUi>>
    suspend fun addUserTrack(uri: String, title: String)
    suspend fun removeUserTrack(id: Long)
}

class AudioRepository(private val dao: AudioTrackDao) : AudioStore {

    override fun userTracks(localeTag: String): Flow<List<UserTrackUi>> =
        dao.observeByKind("USER").map { list -> list.map { it.toUserUi(localeTag) } }

    override fun guides(localeTag: String): Flow<List<GuideTrackUi>> =
        dao.observeByKind("GUIDE").map { list -> list.map { it.toGuideUi(localeTag) } }

    override suspend fun addUserTrack(uri: String, title: String) {
        dao.insertAll(
            listOf(
                AudioTrackEntity(
                    kind = "USER", uri = uri,
                    titleEn = title, titleRu = title, titleHy = title,
                    durationSec = null
                )
            )
        )
    }

    override suspend fun removeUserTrack(id: Long) = dao.deleteUserTrack(id)

    private fun AudioTrackEntity.toUserUi(localeTag: String): UserTrackUi =
        UserTrackUi(id = id, uri = uri.orEmpty(), title = localizedTitle(localeTag))

    private fun AudioTrackEntity.toGuideUi(localeTag: String): GuideTrackUi =
        GuideTrackUi(id = id, title = localizedTitle(localeTag), durationSec = durationSec ?: 0, available = uri != null)

    private fun AudioTrackEntity.localizedTitle(localeTag: String): String {
        val lang = Locale.forLanguageTag(localeTag).language
        return when (lang) {
            "hy" -> titleHy
            "ru" -> titleRu
            else -> titleEn
        }
    }
}
