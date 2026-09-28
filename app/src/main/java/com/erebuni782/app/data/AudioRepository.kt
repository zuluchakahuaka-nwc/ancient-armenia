package com.erebuni782.app.data

import com.erebuni782.app.data.db.AudioTrackDao
import com.erebuni782.app.data.db.AudioTrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

data class UserTrackUi(val id: Long, val uri: String, val title: String)

data class GuideTrackUi(val id: Long, val title: String, val durationSec: Int, val available: Boolean)

data class StationTrack(val assetPath: String, val title: String)

/**
 * D10: Urartu.fm — офлайн-станция из саундтреков владельца (10 мелодий,
 * ~3.5 мин каждая; mp3 в assets/urartu_fm, медиа вне git — AGENTS §gitignore).
 */
val URARTU_FM_PACK: List<StationTrack> = listOf(
    StationTrack("urartu_fm/01_rise_of_the_kingdom_of_van.mp3", "Rise of the Kingdom of Van"),
    StationTrack("urartu_fm/02_sarduri_coronation.mp3", "Sarduri's Coronation"),
    StationTrack("urartu_fm/03_shores_of_lake_van.mp3", "Shores of Lake Van"),
    StationTrack("urartu_fm/04_erebuni_walls_at_dawn.mp3", "Erebuni Walls at Dawn"),
    StationTrack("urartu_fm/05_chariots_of_argishti.mp3", "Chariots of Argishti"),
    StationTrack("urartu_fm/06_teisheba_thunder.mp3", "Teisheba's Thunder"),
    StationTrack("urartu_fm/07_temple_of_musasir.mp3", "Temple of Musasir"),
    StationTrack("urartu_fm/08_khaldi_sacred_fire.mp3", "Khaldi's Sacred Fire"),
    StationTrack("urartu_fm/09_rusa_by_the_araxes.mp3", "Rusa by the Araxes"),
    StationTrack("urartu_fm/10_twilight_of_tushpa.mp3", "Twilight of Tushpa")
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
